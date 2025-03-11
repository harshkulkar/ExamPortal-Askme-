package com.hcoders.portal.security;

// Importing necessary Spring Security and configuration classes
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * WebSecurityConfig is the main security configuration class.
 * <p>
 * This class configures authentication, authorization, password encoding, and session management.
 * It enables session-based authentication, meaning:
 * - When a user logs in, Spring Security creates a session.
 * - The authentication details are stored in the session.
 * - The user stays logged in as long as the session is active.
 * - Logging out invalidates the session.
 * </p>
 */
@Configuration // Marks this class as a Spring configuration class
@EnableWebSecurity // Enables Spring Security in the application
public class WebSecurityConfig {

    /**
     * Configures a UserDetailsService that loads user details from the database.
     * <p>
     * This service is responsible for fetching user information during authentication.
     * </p>
     *
     * @return an instance of UserDetailsService implementation
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(); // Custom implementation for user retrieval
    }

    /**
     * Configures a password encoder to securely hash and verify passwords.
     * <p>
     * Uses the BCrypt algorithm to hash passwords before storing them.
     * </p>
     *
     * @return an instance of BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Secure password hashing algorithm
    }

    /**
     * Configures an authentication provider to authenticate users.
     * <p>
     * - Uses DaoAuthenticationProvider (database-backed authentication)
     * - Retrieves user details using UserDetailsService
     * - Uses BCrypt to check hashed passwords
     * </p>
     *
     * @return an instance of DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // Assigns user details service
        authProvider.setPasswordEncoder(passwordEncoder()); // Sets password encoder
        return authProvider;
    }

    /**
     * Configures the authentication manager that handles authentication requests.
     * <p>
     * - Uses DaoAuthenticationProvider to authenticate users.
     * - Manages authentication using a list of providers.
     * </p>
     *
     * @return an instance of AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider())); // Uses authentication provider for login
    }

    /**
     * Defines security rules for handling HTTP requests and authentication flows.
     * <p>
     * This method:
     * - Secures endpoints based on user roles
     * - Enables session-based authentication (default Spring Security behavior)
     * - Configures login/logout
     * - Handles access-denied situations
     * </p>
     *
     * @param http the HttpSecurity object to configure security settings
     * @return an instance of SecurityFilterChain
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Only users with the "ADMIN" role can access these endpoints
                .requestMatchers("/requests", "/questions", "/users").hasRole("ADMIN")

                // These endpoints are public (accessible without authentication)
                .requestMatchers("/signup**", "/signup", "/confirm-account").permitAll()

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login") // Custom login page
                .permitAll() // Allow everyone to access the login page
            )
            .logout(logout -> logout
                .permitAll() // Allow users to log out without authentication
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error") // Redirects to "/error" if access is denied
            );

        return http.build(); // Builds the security filter chain
    }
}
