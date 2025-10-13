/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.backend.controller;

/**
 *
 * @author lucioamarilla
 */
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

// Permitir peticiones desde un front (por ejemplo, React en 3001)
@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository; // Inyección del repositorio para acceder a la BD

    /**
     * Endpoint POST /login
     * Recibe: { "email": "...", "password": "..." }
     * Valida los datos contra la base de datos.
     * @param request Datos del login (email, password).
     * @return Respuesta con 'success' y 'message'.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1. Validar si el email o password son nulos o vacíos
        if (request.getEmail() == null || request.getPassword() == null ||
            request.getEmail().trim().isEmpty() || request.getPassword().trim().isEmpty()) {
            
            return ResponseEntity.badRequest().body(
                new LoginResponse(false, "Faltan datos de usuario o contraseña.")
            );
        }

        // 2. Buscar el usuario en la base de datos por email y password
        Optional<?> userOptional = userRepository.findByEmailAndPassword(
            request.getEmail(), 
            request.getPassword()
        );

        // 3. Generar la respuesta según el resultado
        if (userOptional.isPresent()) {
            // Usuario encontrado (login correcto)
            return ResponseEntity.ok(
                new LoginResponse(true, "Login correcto")
            );
        } else {
            // Usuario no encontrado o contraseña incorrecta
            return ResponseEntity.ok( 
                new LoginResponse(false, "Usuario o contraseña incorrectos")
            );
        }
    }
}