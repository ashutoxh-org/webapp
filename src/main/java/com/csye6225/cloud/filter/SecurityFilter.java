package com.csye6225.cloud.filter;

import com.csye6225.cloud.model.User;
import com.csye6225.cloud.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * The type Security config.
 */
@Configuration
public class SecurityFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    /**
     * Security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(httpBasicConfigurer -> httpBasicConfigurer
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/user/self").authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable); //CSRF protection can be disabled for APIs where tokens can't easily be managed or are unnecessary due to other security measures like token-based authentication
        return http.build();
    }

    /**
     * Password encoder password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * User details service user details service.
     *
     * @param userRepository the user repository
     * @return the user details service
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList());
        };
    }

    /**
     * To handle 503 when database is down during authentication
     */
    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            if (authException.getCause() instanceof DataAccessException) {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service temporarily unavailable. Please try again later.");
                LOGGER.error("Service temporarily unavailable due to {}", authException.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                LOGGER.error("User {} not authorised due to {}", getEmailFromRequest(request), authException.getMessage());
            }
        }
        private static String getEmailFromRequest(HttpServletRequest request){
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Basic ")) {
                String base64Credentials = header.substring("Basic ".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                final String[] values = credentials.split(":", 2);
                return values[0]; // username
            }
            return "";
        }
    }

}
