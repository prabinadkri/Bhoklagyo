package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.AdminRegisterRequest;
import com.example.Bhoklagyo.dto.AssignOwnerRequest;
import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.Admin;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.repository.AdminRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RestaurantMapper restaurantMapper;

    public AdminService(AdminRepository adminRepository, UserRepository userRepository,
                       RestaurantRepository restaurantRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       RestaurantMapper restaurantMapper) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.restaurantMapper = restaurantMapper;
    }

    public LoginResponse register(AdminRegisterRequest request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setName(request.getName());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setEmail(request.getEmail());
        admin.setPhoneNumber(request.getPhoneNumber());

        Admin savedAdmin = adminRepository.save(admin);

        String token = jwtUtil.generateToken(savedAdmin.getUsername(), "ADMIN");

        return new LoginResponse(token, savedAdmin.getUsername(), "ADMIN", savedAdmin.getId());
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");

        return new LoginResponse(token, admin.getUsername(), "ADMIN", admin.getId());
    }

    @Transactional
    public RestaurantResponse assignOwnerToRestaurant(AssignOwnerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (user.getRole() != Role.OWNER) {
            throw new IllegalArgumentException("User must have OWNER role to be assigned as restaurant owner");
        }

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

        restaurant.setOwner(user);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toResponse(updatedRestaurant);
    }

    public Admin getCurrentAdmin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + username));
    }
}
