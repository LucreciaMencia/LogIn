package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// ⭐️ DTO para recibir el email en el body
public class ForgotPasswordRequest {
    
    @Email(message = "El formato del email no es válido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String email;

    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}