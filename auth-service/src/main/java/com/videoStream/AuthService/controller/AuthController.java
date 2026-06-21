package com.videoStream.AuthService.controller;

import com.videoStream.AuthService.dto.ApiResponse;
import com.videoStream.AuthService.dto.AuthDto.*;
import com.videoStream.AuthService.model.User;
import com.videoStream.AuthService.repository.UserRepository;
import com.videoStream.AuthService.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Username is already taken!"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email is already registered!"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials"));
        }

        String token = jwtUtils.generateToken(user.getUsername());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam("token") String token) {
        boolean isValid = jwtUtils.validateToken(token);
        if (isValid) {
            String username = jwtUtils.getUsernameFromToken(token);
            return ResponseEntity
                    .ok(ApiResponse.success("Token is valid", Map.of("valid", true, "username", username)));
        }
        return ResponseEntity.status(401).body(ApiResponse.error("Token is invalid"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam("token") String token) {
        boolean isValid = jwtUtils.validateToken(token);
        if (isValid) {
            String username = jwtUtils.getUsernameFromToken(token);
            String newToken = jwtUtils.generateToken(username);
            AuthResponse response = new AuthResponse();
            response.setToken(newToken);
            response.setUsername(username);
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
        }
        return ResponseEntity.status(401).body(ApiResponse.error("Token is expired or invalid"));
    }
}
