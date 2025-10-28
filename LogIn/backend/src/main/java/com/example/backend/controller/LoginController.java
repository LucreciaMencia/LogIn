package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
// Sugerencia: Renombrar a AuthController para agrupar lógica de autenticación
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService; 

    @PostMapping("/login")
    
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1. Validar campos vacíos
        if (request.getEmail() == null || request.getPassword() == null ||
            request.getEmail().trim().isEmpty() || request.getPassword().trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                new LoginResponse(false, "Faltan datos de usuario o contraseña.")
            );
        }

        // 2. Buscar usuario por email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        // 3. Verificar usuario y contraseña
        if (userOpt.isEmpty()) {
            // Usuario no encontrado -> 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new LoginResponse(false, "Usuario o contraseña incorrectos")
            );
        }

        User user = userOpt.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 4. Credenciales correctas: Generar Tokens JWT
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // ⭐️ 5. Guardar el Refresh Token en la base de datos
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            // 6. Responder
            return ResponseEntity.ok(
                new LoginResponse(
                    accessToken, 
                    user.getRol(), 
                    jwtService.getExpirationDateTime(accessToken),
                    refreshToken, // Devolver el Refresh Token
                    jwtService.getExpirationDateTime(refreshToken) // Devolver su expiración
                )
            );
        } else {
            // Contraseña incorrecta -> 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new LoginResponse(false, "Usuario o contraseña incorrectos")
            );
        }
    }
}