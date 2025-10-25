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
                // ⭐️ Agregar /api/refresh-token
                .requestMatchers("/register", "/login", "/api/refresh-token").permitAll() 
                .anyRequest().authenticated()
            )
            // ⭐️ CONFIGURACIÓN DE EXCEPCIÓN: Maneja el acceso no autenticado (ej. sin token)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(unauthorizedHandler) // <-- Aquí se define el 401
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }
}