package com.example.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.model.User; // Para acceder al objeto User

@RestController
@CrossOrigin(origins = "http://127.0.0.1:8000/")
public class TestController {

    @GetMapping("/test/protected")
    public String protectedEndpoint() {
        
        // El usuario ya está autenticado si llega a este punto.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Obtener el objeto User cargado por el JwtAuthenticationFilter
        // Esto solo es seguro si sabemos que el principal es nuestro objeto User.
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            
            return "Acceso concedido a la ruta protegida. " +
                   "Usuario autenticado: " + user.getEmail() + 
                   " con Rol: " + user.getRol();
        }
        
        return "Acceso concedido a la ruta protegida. Principal no es User.";
    }
}