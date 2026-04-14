package com.questlog.service;

import com.questlog.model.dto.request.LoginRequest;
import com.questlog.model.dto.request.RegisterRequest;
import com.questlog.model.dto.response.AuthResponse;
import com.questlog.model.entity.User;
import com.questlog.repository.UserRepository;
import com.questlog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        // Check for duplicate email or username
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Build and save the new user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .isOnline(false)
                .build();

        userRepository.save(user);

        // Generate JWT and return response
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }

    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Check password against stored hash
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate JWT and return response
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl()
        );
    }
}