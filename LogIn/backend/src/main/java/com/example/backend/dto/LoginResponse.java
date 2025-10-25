/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.backend.dto;

/**
 *
 * @author lucioamarilla
 */

import java.time.LocalDateTime;

public class LoginResponse {
    private boolean success;
    private String message;
    
    // Campos para JWT de Acceso
    private String token; // Access Token
    private String rol;
    private LocalDateTime expiraEn; // Expiración del Access Token

    // ⭐️ Nuevo campo para JWT de Refresco
    private String refreshToken;
    private LocalDateTime refreshTokenExpiraEn;

    // Constructor original (para errores sin token)
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // ⭐️ Nuevo constructor para login exitoso con tokens
    public LoginResponse(String token, String rol, LocalDateTime expiraEn, String refreshToken, LocalDateTime refreshTokenExpiraEn) {
        this.success = true;
        this.message = "Login correcto";
        this.token = token;
        this.rol = rol;
        this.expiraEn = expiraEn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiraEn = refreshTokenExpiraEn;
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(LocalDateTime expiraEn) {
        this.expiraEn = expiraEn;
    }
    
    // ⭐️ Getters y Setters para Refresh Token
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getRefreshTokenExpiraEn() {
        return refreshTokenExpiraEn;
    }

    public void setRefreshTokenExpiraEn(LocalDateTime refreshTokenExpiraEn) {
        this.refreshTokenExpiraEn = refreshTokenExpiraEn;
    }
}