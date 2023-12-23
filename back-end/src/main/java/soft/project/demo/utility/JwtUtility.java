package soft.project.demo.utility;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
		claims.put("authorities", userDetails.getAuthorities()
				.stream()
				.map(auth -> auth.getAuthority())
				.collect(Collectors.toList()));
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
	
	public Boolean validateToken (String token, UserDetails userDetails) {
		if (!StringUtils.hasText(token) || isTokenRevoked(token))
            return false;
		final String username = getUsernameFromToken(token);
		return (userDetails != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
