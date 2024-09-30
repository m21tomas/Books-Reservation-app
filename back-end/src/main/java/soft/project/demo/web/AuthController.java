package soft.project.demo.web;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.util.Duration;
import io.jsonwebtoken.ExpiredJwtException;
import soft.project.demo.dto.AuthCredentialsRequest;
import soft.project.demo.model.RevokedToken;
import soft.project.demo.model.User;
import soft.project.demo.service.RevokedTokensService;
import soft.project.demo.utility.JwtUtility;

@RestController
@RequestMapping("/api/auth")
//@Api(value = "Authentification Controller")
public class AuthController {
	
	private Logger LOG = LoggerFactory.getLogger(AuthController.class);
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtility jwtUtil;
	@Value("${cookies.domain}")
    private String domain;
	@Autowired
	private RevokedTokensService revokedService;
	
	@PostMapping("/login")
	//@ApiOperation(value = "Login")
	public ResponseEntity<?> login (
			 @RequestBody AuthCredentialsRequest request){
		try {
            Authentication authenticate = authenticationManager
            	.authenticate(
                     new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                    )
                );

            User user = (User) authenticate.getPrincipal();
            
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            
            String rolesAsString = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
            
            user.setPassword(null);
            
            String token = jwtUtil.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .domain(domain)
                    .path("/")
                    .maxAge(Duration.buildByHours(24).getMilliseconds()/1000)
                    .build();
            LOG.info("LOGIN, user: {}, authority: {}", user.getUsername(), rolesAsString);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(token);

        } catch (BadCredentialsException ex) {
        	LOG.error("LOGIN failed, \n exception: {}", ex.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

	}
	
	@GetMapping("/validate")
	//@ApiOperation(value = "Validate token")
	public ResponseEntity<?> validateToken (@CookieValue(name = "jwt") String token, @AuthenticationPrincipal User user) {
		try {
	        if (user != null) {
	            Boolean isTokenValid = jwtUtil.validateToken(token, user);
	            if(isTokenValid)
	            LOG.info("User: "+user.getUsername() +", validation successful");
	            else
	            LOG.warn("User: "+user.getUsername() +", validation failed");
	            return new ResponseEntity<Boolean>(isTokenValid, HttpStatus.OK);
	        } else {
	            LOG.warn("User not found for token validation ");
	            return ResponseEntity.ok(false);
	        }
	    }
		catch (ExpiredJwtException e) {
			LOG.error("VALIDATE failed, token expired \n response exception: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
	}
	
	@GetMapping("/logout")
	//@ApiOperation(value = "Logout of the Book Reservation Application")
    public ResponseEntity<?> logout (
    	@CookieValue(name = "jwt") String token, @AuthenticationPrincipal User user) {
		RevokedToken revokedToken = revokedService.createRevokedToken(token, user);
		if(revokedService.getRevokedTokenById(revokedToken.getId()).getToken().equals(token)) {
			LOG.info("User: {}. The token is revoked", user.getUsername());
		}
		else {
			LOG.error("User: {}. The token is not revoked", user.getUsername());
		}
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .domain(domain)
                .path("/")
                .maxAge(0)
                .build();
        LOG.info("LOGOUT, user: {}", user.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("ok");
    }
}
