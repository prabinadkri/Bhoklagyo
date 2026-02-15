package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RegisterRequest;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.CUSTOMER);
        testUser.setPhoneNumber("+977-9812345678");
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void loginSuccess() {
            LoginRequest request = new LoginRequest("john@example.com", "password123");
            Authentication auth = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken("john@example.com", "CUSTOMER")).thenReturn("jwt-token");

            LoginResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            assertEquals("john@example.com", response.getEmail());
            assertEquals("John Doe", response.getName());
            assertEquals("CUSTOMER", response.getRole());
            assertEquals(1L, response.getUserId());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository).findByEmail("john@example.com");
        }

        @Test
        @DisplayName("Should throw BadCredentialsException for invalid credentials")
        void loginFailsWithBadCredentials() {
            LoginRequest request = new LoginRequest("john@example.com", "wrongPassword");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThrows(BadCredentialsException.class, () -> authService.login(request));
            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Should throw when user not found after authentication")
        void loginFailsWhenUserNotFound() {
            LoginRequest request = new LoginRequest("ghost@example.com", "password123");
            Authentication auth = mock(Authentication.class);

            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> authService.login(request));
        }
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register a new customer successfully")
        void registerSuccess() {
            RegisterRequest request = new RegisterRequest(
                    "Jane Doe", "Password1", "jane@example.com", "+977-9800000001", Role.CUSTOMER, "Kathmandu");

            when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("Password1")).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(2L);
                return user;
            });
            when(jwtUtil.generateToken("jane@example.com", "CUSTOMER")).thenReturn("new-jwt-token");

            LoginResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals("new-jwt-token", response.getToken());
            assertEquals("jane@example.com", response.getEmail());
            assertEquals("Jane Doe", response.getName());
            assertEquals("CUSTOMER", response.getRole());

            verify(userRepository).save(argThat(user ->
                    user.getEmail().equals("jane@example.com") &&
                    user.getPassword().equals("encoded") &&
                    user.getRole() == Role.CUSTOMER
            ));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException for existing email")
        void registerFailsWithDuplicateEmail() {
            RegisterRequest request = new RegisterRequest(
                    "John Doe", "Password1", "john@example.com", null, Role.CUSTOMER, null);

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

            assertThrows(DuplicateResourceException.class, () -> authService.register(request));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should register an owner successfully")
        void registerOwnerSuccess() {
            RegisterRequest request = new RegisterRequest(
                    "Bob Owner", "Password1", "bob@example.com", "+977-9811111111", Role.OWNER, null);

            when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("Password1")).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(3L);
                return user;
            });
            when(jwtUtil.generateToken("bob@example.com", "OWNER")).thenReturn("owner-token");

            LoginResponse response = authService.register(request);

            assertEquals("OWNER", response.getRole());
            verify(userRepository).save(argThat(user -> user.getRole() == Role.OWNER));
        }
    }
}
