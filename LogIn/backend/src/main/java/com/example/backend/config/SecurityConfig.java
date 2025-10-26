package com.example.backend.config;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 

@Configuration
public class SecurityConfig {

    @Autowired 
    private JwtAuthenticationFilter jwtAuthFilter; 
    
    @Autowired 
    private JwtAuthenticationEntryPoint unauthorizedHandler; 

    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // ⭐️ AGREGADO: /reset-password
                .requestMatchers("/register", "/login", "/api/refresh-token", "/forgot-password", "/reset-password").permitAll() 
                .anyRequest().authenticated() 
            )
            .exceptionHandling(exceptions -> exceptions 
                .authenticationEntryPoint(unauthorizedHandler) 
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build(); 
    }
}