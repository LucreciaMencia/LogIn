package com.example.backend.config; // O com.example.backend.filter

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull; 


import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserRepository userRepository;

@Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Verificar encabezado y formato Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token
        jwt = authHeader.substring(7);
        
        try {
            // ⭐️ Verificar el tipo de token antes de procesarlo
            String tokenType = jwtService.extractTokenType(jwt);
            if (!"ACCESS".equals(tokenType)) {
                 // Permitir que la cadena de filtros continúe para que el EntryPoint maneje el 401
                filterChain.doFilter(request, response);
                return;
            }
            
            // 2. Extraer email del Subject y validar si ya está autenticado
            userEmail = jwtService.extractUsername(jwt); 
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 3. Buscar usuario en la base de datos (por email)
                Optional<User> userOpt = userRepository.findByEmail(userEmail);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // 4. Validar el token y autenticar
                    if (jwtService.isTokenValid(jwt, user)) {
                        
                        // Creación del objeto de autenticación
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user, // Principal (el objeto User)
                                null, // Credentials (nulo para tokens ya validados)
                                Collections.emptyList() // Authorities (Simplificación: sin roles específicos por ahora, aunque podría usar 'user.getRol()' para esto)
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        // 5. Establecer la autenticación en el SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            
            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            // 6. Manejo de errores de token (expirado, inválido, etc.)
            System.err.println("Error de JWT: " + e.getMessage());
            // ⭐️ El EntryPoint debe manejar los 401 para los tokens inválidos si no se autentica
            // Aquí solo logueamos el error y permitimos que la cadena continúe, 
            // el EntryPoint es el que finalmente responde con el 401 si no hay autenticación.
            
            // Si quieres responder inmediatamente con 401 al detectar un error de parseo:
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            // response.getWriter().write("{\"message\": \"Token JWT inválido o expirado\"}");
            // response.setContentType("application/json");
            // return;
            
            // Dejamos que el EntryPoint maneje la respuesta 401 para consistencia.
            filterChain.doFilter(request, response);
        }
    }
}