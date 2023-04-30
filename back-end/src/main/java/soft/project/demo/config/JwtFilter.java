package soft.project.demo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import soft.project.demo.repository.UserRepository;
import soft.project.demo.utility.JwtUtility;

@Component
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private JwtUtility jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		/*
		// Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if ((StringUtils.hasText(header) && !header.startsWith("Bearer ")) ||
        	 !StringUtils.hasText(header)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Get jwt token
        final String token = header.split(" ")[1].trim();

        // Get user identity and set it on the spring security context
        UserDetails userDetails = userRepo
        		.findByUsername(jwtUtil.getUsernameFromToken(token))
        		.orElse(null);
        */
		
		if (request.getCookies() == null) {
            chain.doFilter(request, response);
            return;
        }
		
		// Get authorization header
        Optional<Cookie> jwtOpt = Arrays.stream(request.getCookies())
              .filter(cookie -> "jwt".equals(cookie.getName()))
              .findAny();
        
        if (jwtOpt.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
		
        String token = jwtOpt.get().getValue();
        UserDetails userDetails = null;
        try {
            userDetails = userRepo
            		.findByUsername(jwtUtil.getUsernameFromToken(token))
            		.orElse(null);
        } catch (ExpiredJwtException | SignatureException e) {
            chain.doFilter(request, response);
            return;
        }
        
        // Validate jwt token
        if (!jwtUtil.validateToken(token, userDetails)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken
            authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ?
                    List.of() : userDetails.getAuthorities()
            );

        authentication.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
	}
}
