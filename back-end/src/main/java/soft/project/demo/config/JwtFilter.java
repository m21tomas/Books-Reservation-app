package soft.project.demo.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
