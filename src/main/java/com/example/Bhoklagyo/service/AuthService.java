package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RegisterRequest;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(LoginRequest request) {
        System.out.println("=== LOGIN START ===");
        try {
            System.out.println("=== BEFORE AUTHENTICATION ===");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            System.out.println("=== AFTER AUTHENTICATION ===");
        } catch (AuthenticationException e) {
            System.out.println("=== AUTHENTICATION FAILED ===");
            throw new BadCredentialsException("Invalid email or password");
        }

        System.out.println("=== BEFORE FIND USER ===");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("=== AFTER FIND USER ===");

        System.out.println("=== BEFORE GENERATE TOKEN ===");
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        System.out.println("=== AFTER GENERATE TOKEN ===");

        System.out.println("=== BEFORE CREATE RESPONSE ===");
        return new LoginResponse(token, user.getEmail(), user.getName(), user.getRole().name(), user.getId());
    }

    public LoginResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Validate role-specific requirements
        

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        
        
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return new LoginResponse(token, savedUser.getEmail(), savedUser.getName(), savedUser.getRole().name(), savedUser.getId());
    }
}
