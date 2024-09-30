package soft.project.demo.utility;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import soft.project.demo.model.RevokedToken;
import soft.project.demo.repository.RevokedTokenRepository;

@Component
public class JwtUtility implements Serializable {
	
	private static final long serialVersionUID = -5551412898633203438L;
	
	private Logger LOG = LoggerFactory.getLogger(JwtUtility.class);
	
	@Autowired
    private RevokedTokenRepository revokedTokenRepository;
	
	public static final long JWT_TOKEN_VALIDITY = 1*24*60*60; //days*hours*minutes*seconds
	
	@Value("${jwt.secret}")
	private String secret;
	
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}
	
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getAuthoritiesFromToken(String token) {
	    return getClaimFromToken(token, claims -> (Collection<String>) claims.get("authorities"));
	}
	
	@SuppressWarnings("unchecked")
	public Collection<? extends GrantedAuthority> getRolesFromToken(String token) {
	    Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	    return (Collection<? extends GrantedAuthority>) claims.get("authorities");
	}

	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	private Boolean isTokenExpired (String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	// Check if a token is revoked
	boolean isTokenRevoked(String token, LocalDateTime dateTime) {
		if(dateTime == null) dateTime = LocalDateTime.now();
	    return revokedTokenRepository.existsByTokenAndRevocationDateBeforeOrEqualTo(token, dateTime);
	}
	
	boolean isTokenRevoked(String token) {
	    return revokedTokenRepository.existsByToken(token);
	}
	
	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}
	
	@Transactional
	public void revokeToken(String token) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setRevocationDate(LocalDateTime.now());
        revokedToken.setExpirationDate(getExpirationDateFromToken(token).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        revokedTokenRepository.save(revokedToken);
    }
	
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		Set<String> distinctAuthorities = userDetails.getAuthorities()
	            .stream()
	            .map(GrantedAuthority::getAuthority)
	            .collect(Collectors.toSet());

	    claims.put("authorities", distinctAuthorities);
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		//LOG.info("Validating token: {}", token);
	    if (!StringUtils.hasText(token) || !token.startsWith("eyJ")) {
	        LOG.warn("Invalid token format or empty: {}", token);
	        return false;
	    }
	    
	    if (isTokenRevoked(token)) {
	        LOG.warn("The token is revoked");
	        return false;
	    }
	    
	    final String username = getUsernameFromToken(token);
	    if (userDetails == null) {
	        LOG.warn("UserDetails is null");
	        return false;
	    }

	    if (!username.equals(userDetails.getUsername())) {
	        LOG.warn("Username from token does not match UserDetails username");
	        return false;
	    }

	    if (isTokenExpired(token)) {
	        LOG.warn("Token has expired");
	        return false;
	    }

	    return true;
	}


}
