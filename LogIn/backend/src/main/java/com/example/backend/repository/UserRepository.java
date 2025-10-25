/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.backend.repository;

/**
 *
 * @author lucioamarilla
 */
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Método personalizado para encontrar un usuario por email y contraseña
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);

}
