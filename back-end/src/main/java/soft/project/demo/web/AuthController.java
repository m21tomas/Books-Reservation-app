package soft.project.demo.web;

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
import soft.project.demo.model.User;
import soft.project.demo.utility.JwtUtility;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private Logger LOG = LoggerFactory.getLogger(AuthController.class);
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtility jwtUtil;
	@Value("${cookies.domain}")
    private String domain;
	
	@PostMapping("/login")
	public ResponseEntity<?> login (@RequestBody AuthCredentialsRequest request){
		try {
            Authentication authenticate = authenticationManager
            	.authenticate(
                     new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                    )
                );

            User user = (User) authenticate.getPrincipal();
            user.setPassword(null);
            
            String token = jwtUtil.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .domain(domain)
                    .path("/")
                    .maxAge(Duration.buildByHours(24).getMilliseconds()/1000)
                    .build();
            LOG.info("LOGIN successful, \n response token: {}", token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(token);
            /*
            return ResponseEntity.ok()
                    .header(
                    HttpHeaders.AUTHORIZATION,
                    jwtUtil.generateToken(user))
                    .body(user);
            */
        } catch (BadCredentialsException ex) {
        	LOG.error("LOGIN failed, \n exception: {}", ex.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

	}
	
	@GetMapping("/validate")
	public ResponseEntity<?> validateToken (@CookieValue(name = "jwt") String token, @AuthenticationPrincipal User user) {
		LOG.info("/validate endpoint reached");
		try {
			Boolean isTokenValid = jwtUtil.validateToken(token, user);
			LOG.info("VALIDATE successful, response boolean: {}", isTokenValid.toString());
			return new ResponseEntity<Boolean>(isTokenValid, HttpStatus.OK);
		}
		catch (ExpiredJwtException e) {
			LOG.error("VALIDATE failed, token expired \n response exception: {}", e.toString());
            return ResponseEntity.ok(false);
        }
	}
	
	@GetMapping("/logout")
    public ResponseEntity<?> logout () {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .domain(domain)
                .path("/")
                .maxAge(0)
                .build();
        LOG.info("LOGOUT successful, \n response boolean: {}", cookie.toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("ok");
    }
}
