package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.AdminDashboardResponse;
import com.example.Bhoklagyo.dto.AdminRegisterRequest;
import com.example.Bhoklagyo.dto.AssignOwnerRequest;
import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.Admin;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.mapper.UserMapper;
import com.example.Bhoklagyo.repository.AdminRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.JwtUtil;

import jakarta.persistence.EntityManager;

import java.io.File;

import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Bhoklagyo.repository.OrderRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.dto.UserResponse;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    @Value("${admin.registration.secret:}")
    private String adminRegistrationSecret;

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RestaurantMapper restaurantMapper;
    private final OrderRepository orderRepository;
    private final RestaurantMenuItemRepository menuItemRepository;
    private final UserMapper userMapper;
    private final EntityManager em;
    

    public AdminService(AdminRepository adminRepository, UserRepository userRepository,
                       RestaurantRepository restaurantRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       RestaurantMapper restaurantMapper, OrderRepository orderRepository,RestaurantMenuItemRepository menuItemRepository, UserMapper userMapper, EntityManager em) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.restaurantMapper = restaurantMapper;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.userMapper = userMapper;
        this.em = em;
    }

    public LoginResponse register(AdminRegisterRequest request) {
        // Validate admin registration secret
        if (adminRegistrationSecret.isBlank()) {
            throw new IllegalStateException(
                    "Admin registration is disabled. Set ADMIN_REGISTRATION_SECRET environment variable.");
        }
        if (!adminRegistrationSecret.equals(request.getRegistrationSecret())) {
            log.warn("Invalid admin registration attempt for email: {}", request.getEmail());
            throw new AccessDeniedException("Invalid registration secret");
        }

        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setEmail(request.getEmail());
        admin.setPhoneNumber(request.getPhoneNumber());

        Admin savedAdmin = adminRepository.save(admin);

        String token = jwtUtil.generateToken(savedAdmin.getEmail(), "ADMIN");

        return new LoginResponse(token, savedAdmin.getEmail(), savedAdmin.getName(), "ADMIN", savedAdmin.getId());
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtUtil.generateToken(admin.getEmail(), "ADMIN");

        return new LoginResponse(token, admin.getEmail(), admin.getName(), "ADMIN", admin.getId());
    }

    @Transactional
    public RestaurantResponse assignOwnerToRestaurant(AssignOwnerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Admin has authority to change user role to OWNER when assigning to restaurant
        if (user.getRole() != Role.OWNER) {
            user.setRole(Role.OWNER);
            userRepository.save(user);
        }

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

        restaurant.setOwner(user);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toResponse(updatedRestaurant);
    }

    public Admin getCurrentAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + email));
    }
    public AdminDashboardResponse getAdminDashboard() {
        long totalUsers = userRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long totalOrders = orderRepository.count();
        String sql="SELECT COALESCE(SUM(oi.price_at_order * oi.quantity),0) FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.status IN ('DELIVERED', 'COMPLETED')";
        Query query = em.createNativeQuery(sql);
        Object total = query.getSingleResult();
        if (total == null) {return  null;}
        double totalRevenue = ((Number) total).doubleValue();
        return new AdminDashboardResponse(totalUsers, totalRestaurants, totalOrders, totalRevenue);
    }
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
    }
    @Transactional
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        menuItemRepository.deleteByRestaurantId(restaurantId);
        
        restaurantRepository.delete(restaurant);
}
    @Transactional
    public UserResponse changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
}
