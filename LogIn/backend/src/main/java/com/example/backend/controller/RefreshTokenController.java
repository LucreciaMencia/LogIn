package com.example.backend.controller;

import com.example.backend.dto.LoginResponse; // Reutilizar LoginResponse para la respuesta
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

// ⭐️ DTO para recibir el token de refresco
class RefreshTokenRequest {
    private String refreshToken;
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:8000/")
public class RefreshTokenController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        final String refreshToken = request.getRefreshToken();
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new LoginResponse(false, "El token de refresco es obligatorio.")
            );
        }
        
        try {
            // 1. Extraer email del Refresh Token
            final String userEmail = jwtService.extractUsername(refreshToken);

            // 2. Buscar usuario y verificar validez (no expirado)
            if (userEmail != null) {
                Optional<User> userOpt = userRepository.findByEmail(userEmail);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // 3. Validar el token de refresco:
                    //    a. No expirado
                    //    b. Coincide con el almacenado en la DB (no revocado)
                    //    c. Es un token de tipo REFRESH
                    
                    if (jwtService.isTokenValid(refreshToken, user) && 
                        refreshToken.equals(user.getRefreshToken()) && // ⭐️ Verificación de revocación
                        "REFRESH".equals(jwtService.extractTokenType(refreshToken))) {

                        // 4. Generar nuevo Access Token (el Refresh Token se mantiene)
                        String newAccessToken = jwtService.generateAccessToken(user);

                        // 5. Devolver nueva respuesta con el nuevo Access Token
                        return ResponseEntity.ok(
                            new LoginResponse(
                                newAccessToken, 
                                user.getRol(), 
                                jwtService.getExpirationDateTime(newAccessToken),
                                user.getRefreshToken(), // El Refresh Token no cambia
                                jwtService.getExpirationDateTime(user.getRefreshToken())
                            )
                        );
                    }
                }
            }
            
            // Si el token es inválido (expirado, usuario no encontrado, no coincide con DB)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( // 403 Forbidden para token revocado o inválido
                new LoginResponse(false, "Token de refresco inválido o revocado.")
            );

        } catch (JwtException e) {
             // Si el token no es parseable (formato incorrecto, firma inválida, etc.)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( // 403 Forbidden para JWT no válido
                new LoginResponse(false, "Token de refresco inválido o expirado.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new LoginResponse(false, "Error interno al procesar el token de refresco.")
            );
        }
    }
}