// src/main/java/com/example/backend/service/JwtService.java
package com.example.backend.service;

import com.example.backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims; // <--- Importante

import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:tu_clave_secreta_de_32_o_mas_caracteres_aqui}") //
    private String secret; //

    // ⭐️ TIEMPOS DE VALIDEZ
    // Token de Acceso: 15 minutos (900,000 milisegundos)
    private final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 15 * 60; // Corregido a 15 minutos (900,000)
    // Token de Refresco: 7 días (604,800,000 milisegundos)
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //
    
    // ⭐️ 1. Inyectar la nueva propiedad
    @Value("${jwt.reset-password.expiration.ms}")
    private long resetPasswordExpirationMs;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret); //
        return Keys.hmacShaKeyFor(keyBytes); //
    }

    /**
     * Genera el JWT de ACCESO (corto tiempo de vida).
     */
    public String generateAccessToken(User user) { 
        Map<String, Object> claims = new HashMap<>(); //
        claims.put("id", user.getId()); //
        claims.put("rol", user.getRol()); //
        claims.put("token_type", "ACCESS"); //

        return Jwts.builder() //
            .setClaims(claims) //
            .setSubject(user.getEmail()) //
            .setIssuedAt(new Date(System.currentTimeMillis())) //
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) //
            .signWith(getSignInKey(), SignatureAlgorithm.HS256) //
            .compact(); //
    }
    
    /**
     * Genera el JWT de REFRESCO (largo tiempo de vida).
     */
    public String generateRefreshToken(User user) { 
        Map<String, Object> claims = new HashMap<>(); //
        claims.put("id", user.getId()); //
        claims.put("rol", user.getRol()); //
        claims.put("token_type", "REFRESH"); //

        return Jwts.builder() //
            .setClaims(claims) //
            .setSubject(user.getEmail()) //
            .setIssuedAt(new Date(System.currentTimeMillis())) //
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME)) //
            .signWith(getSignInKey(), SignatureAlgorithm.HS256) //
            .compact(); //
    }
    
    /**
     * ⭐️ NUEVO: Genera el JWT de RECUPERACIÓN de contraseña (corto plazo).
     */
    public String generatePasswordResetToken(User user) {
        
        // El token expira en base al valor configurado
        Date expirationDate = new Date(System.currentTimeMillis() + resetPasswordExpirationMs); 
        
        // Claims adicionales para identificar el tipo de token
        Map<String, Object> claims = Map.of("token_type", "PASSWORD_RESET");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate) // ⭐️ USAMOS LA FECHA DE EXPIRACIÓN
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Método auxiliar para obtener la fecha de expiración como LocalDateTime (para la respuesta)
    public LocalDateTime getExpirationDateTime(String token) { //
        Date expirationDate = extractExpiration(token); //
        
        return expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); //
    }
    
// ... métodos auxiliares que permanecen iguales ...
    private Claims extractAllClaims(String token) { //
        return Jwts //
                .parserBuilder() //
                .setSigningKey(getSignInKey()) //
                .build() //
                .parseClaimsJws(token) //
                .getBody(); //
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { //
        final Claims claims = extractAllClaims(token); //
        return claimsResolver.apply(claims); //
    }
    
    // Método para obtener el email (Subject)
    public String extractUsername(String token) { //
        return extractClaim(token, Claims::getSubject); //
    }
    
    // Método para obtener la fecha de expiración
    private Date extractExpiration(String token) { //
        return extractClaim(token, Claims::getExpiration); //
    }
    
    // Método para verificar si el token ha expirado
    private boolean isTokenExpired(String token) { //
        return extractExpiration(token).before(new Date()); //
    }
    
    // Método para validar el token
    public boolean isTokenValid(String token, User user) { //
        final String username = extractUsername(token); //
        return (username.equals(user.getEmail())) && !isTokenExpired(token); //
    }
    
    // Método para verificar el tipo de token (útil para el filtro y refresh)
    public String extractTokenType(String token) { //
        return extractClaim(token, claims -> claims.get("token_type", String.class)); //
    }
}