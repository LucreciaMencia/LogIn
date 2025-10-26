package com.example.backend.controller;

import com.example.backend.dto.ForgotPasswordRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Map;
import java.util.Collections;

@RestController
@Validated
@CrossOrigin(origins = "http://127.0.0.1:8000/")
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    // La URL de frontend donde el usuario completará el cambio de contraseña
    private static final String FRONTEND_RESET_URL = "http://localhost:3000/reset-password?token=";

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        
        final String email = request.getEmail();
        
        try {
            // 1. Validar existencia del usuario por email
            Optional<User> userOpt = userRepository.findByEmail(email); //

            if (userOpt.isEmpty()) {
                // 404 Not Found: Correo no registrado
                Map<String, String> errorResponse = Collections.singletonMap("error", "No existe un usuario registrado con ese correo electrónico.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            User user = userOpt.get();

            // 2. Generar el token temporal de recuperación (JWT)
            String resetToken = jwtService.generatePasswordResetToken(user);

            // 3. Guardar el token en la base de datos (reutilizamos el campo refreshToken)
            // ⚠️ Nota: Es crucial manejar esto para no sobreescribir un Refresh Token activo
            // Si el campo 'refreshToken' es usado para ambos, al solicitar un nuevo token
            // de reseteo, se revoca el Refresh Token anterior.
            user.setRefreshToken(resetToken); //
            userRepository.save(user); //
            
            // 4. Responder con éxito y el enlace (simulando envío de correo)
            Map<String, String> successResponse = Map.of(
                "message", "Se generó correctamente el enlace de recuperación.",
                "reset_link", FRONTEND_RESET_URL + resetToken
            );

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            // 5. Manejo de errores internos (ej. fallo de DB al guardar)
            e.printStackTrace();
            Map<String, String> errorResponse = Collections.singletonMap("error", "Ocurrió un error al generar el token de recuperación. Intente nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}