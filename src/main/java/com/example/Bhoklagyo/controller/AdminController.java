package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.AdminRegisterRequest;
import com.example.Bhoklagyo.dto.AssignOwnerRequest;
import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.Bhoklagyo.dto.AdminDashboardResponse;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.dto.RoleUpdateRequest;
import com.example.Bhoklagyo.dto.UserResponse;
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody AdminRegisterRequest request) {
        LoginResponse response = adminService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = adminService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-owner")
    public ResponseEntity<RestaurantResponse> assignOwner(@RequestBody AssignOwnerRequest request) {
        RestaurantResponse response = adminService.assignOwnerToRestaurant(request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long userId, @RequestBody RoleUpdateRequest request) {
        var response = adminService.changeUserRole(userId, request.getNewRole());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        AdminDashboardResponse response = adminService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    
    }
    @DeleteMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        adminService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}