package soft.project.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletResponse;
import soft.project.demo.utility.CustomPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable global method security
public class SecurityConfig {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	CustomPasswordEncoder passwordEncoder;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	     return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Autowired
	protected void configureAuthentication (AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder.getPasswordEncoder());
	}
	
	@Bean
	protected SecurityFilterChain configureAuthorization (HttpSecurity http) throws Exception{		
		/*
		http.cors().and().csrf().disable()
		    .authorizeHttpRequests()
		    .requestMatchers("/api/auth/**", "/api/verify").permitAll()
		    .anyRequest().authenticated()
			.and()
			.sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    .exceptionHandling()
			.authenticationEntryPoint((request, response, ex) -> {
				   response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
			})
			.and()
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);	
		return http.build();
		*/
		
		return http.cors(cors -> corsFilter()).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
	            .requestMatchers("/api/auth/**", "/api/verify").permitAll()
	            .anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, ex) -> {
					   response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
				}))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)	
	            .build();
	   
		/*
		return http.cors(cors -> corsFilter()).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
			            .requestMatchers("/**").permitAll()
			            )
                .build();
        */
	}

    // Used by Spring Security if CORS is enabled.
    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("Set-Cookie");
        config.addExposedHeader("Authorization");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
