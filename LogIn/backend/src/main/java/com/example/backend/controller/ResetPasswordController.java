package com.example.backend.controller;

import com.example.backend.dto.ResetPasswordRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@CrossOrigin(origins = "http://127.0.0.1:8000/")
public class ResetPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Usamos el bean ya configurado

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        
        final String resetToken = request.getToken();
        final String newPassword = request.getNewPassword();
        
        // Causa: Faltan campos requeridos (manejo por @Valid y @NotBlank en DTO)
        if (resetToken == null || resetToken.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Collections.singletonMap("error", "Debe proporcionar el token y la nueva contraseña.")
            );
        }

        try {
            // 1. Extraer email del token
            final String userEmail = jwtService.extractUsername(resetToken);

            if (userEmail == null) {
                // Causa: Token inválido (no tiene subject/email)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("error", "El enlace de recuperación no es válido o ha expirado.")
                );
            }

            // 2. Buscar usuario
            Optional<User> userOpt = userRepository.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                // Causa: No se encuentra el usuario asociado al token.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("error", "Usuario no encontrado para este token.")
                );
            }

            User user = userOpt.get();

            // 3. Validar el token:
            //    a. No expirado
            //    b. Coincide con el almacenado en la DB (no revocado por un nuevo forgot-password)
            //    c. Es un token de tipo PASSWORD_RESET (debes asegurarte que tu JwtService lo marca así)
            // 
            // ⚠️ Nota: Asumo que en tu JwtService tienes un método isTokenValid que valida expiración.

            if (jwtService.isTokenValid(resetToken, user) && 
                resetToken.equals(user.getRefreshToken()) && // ⭐️ El token debe coincidir con el almacenado
                "PASSWORD_RESET".equals(jwtService.extractTokenType(resetToken))) {
                
                // 4. Actualizar contraseña (almacenada de forma segura)
                user.setPassword(passwordEncoder.encode(newPassword));
                
                // 5. Invalidar el token para evitar reuso (anulando el campo refreshToken)
                user.setRefreshToken(null); 
                
                userRepository.save(user);

                // Causa: Éxito
                return ResponseEntity.ok(
                    Collections.singletonMap("message", "Tu contraseña ha sido restablecida correctamente.")
                );

            } else {
                // Causa: Token inválido, manipulado o expirado. (Fallo en isTokenValid o en la coincidencia con DB)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("error", "El enlace de recuperación no es válido o ha expirado.")
                );
            }

        } catch (JwtException e) {
            // Causa: Token inválido o manipulado (ej. firma incorrecta, formato roto)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Collections.singletonMap("error", "El enlace de recuperación no es válido o ha expirado.")
            );
        } catch (Exception e) {
            // Causa: Error al actualizar la contraseña en la base de datos o cualquier otro error interno
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Collections.singletonMap("error", "No se pudo restablecer la contraseña. Intente nuevamente.")
            );
        }
    }
}