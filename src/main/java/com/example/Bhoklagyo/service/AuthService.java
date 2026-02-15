package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RegisterRequest;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLog;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       AuditLogService auditLog) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.auditLog = auditLog;
    }

    public LoginResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email: {}", request.getEmail());
            auditLog.log(AuditLogService.AuditAction.USER_LOGIN_FAILED, request.getEmail(),
                    Map.of("reason", "bad_credentials"));
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        log.info("User logged in successfully: {}", request.getEmail());
        auditLog.log(AuditLogService.AuditAction.USER_LOGIN_SUCCESS, request.getEmail(),
                Map.of("userId", user.getId(), "role", user.getRole().name()));

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

        auditLog.log(AuditLogService.AuditAction.USER_REGISTERED, savedUser.getEmail(),
                Map.of("userId", savedUser.getId(), "role", savedUser.getRole().name()));

        return new LoginResponse(token, savedUser.getEmail(), savedUser.getName(), savedUser.getRole().name(), savedUser.getId());
    }
}
