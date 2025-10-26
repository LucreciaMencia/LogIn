package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    
    @NotBlank(message = "El token es obligatorio.")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String newPassword;

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}