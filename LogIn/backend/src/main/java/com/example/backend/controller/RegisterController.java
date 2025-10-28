package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@Validated
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    
    public ResponseEntity<?> register(@Valid @RequestBody User userRequest) {

        // 1. Verificar si ya existe un usuario con ese email
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(
                new ResponseMessage("El usuario ya está registrado")
            );
        }

        // 2. Encriptar contraseña y completar datos
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRequest.setFechaCreacion(LocalDateTime.now());
        userRequest.setActivo(true);
        if (userRequest.getRol() == null) userRequest.setRol("USER");

        // 3. Guardar usuario
        userRepository.save(userRequest);

        // 4. Responder
        return ResponseEntity.status(201).body(
            new ResponseMessage("Usuario registrado correctamente")
        );
    }

    // Clase interna para devolver un mensaje JSON simple
    static class ResponseMessage {
        private String message;
        public ResponseMessage(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
