package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.config.JwtAuthenticationFilter;
import com.example.Bhoklagyo.config.RateLimitingFilter;
import com.example.Bhoklagyo.dto.LoginRequest;
import com.example.Bhoklagyo.dto.LoginResponse;
import com.example.Bhoklagyo.dto.RegisterRequest;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.exception.GlobalExceptionHandler;
import com.example.Bhoklagyo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private RateLimitingFilter rateLimitingFilter;

    @Nested
    @DisplayName("POST /auth/login")
    class LoginEndpoint {

        @Test
        @DisplayName("200 with valid credentials")
        void loginSuccess() throws Exception {
            LoginRequest req = new LoginRequest("user@example.com", "Password1");
            LoginResponse resp = new LoginResponse("jwt-token", "user@example.com", "User", "CUSTOMER", 1L);
            when(authService.login(any(LoginRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"))
                    .andExpect(jsonPath("$.email").value("user@example.com"));
        }

        @Test
        @DisplayName("400 when email is blank")
        void loginBlankEmail() throws Exception {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"\", \"password\":\"Password1\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when email is invalid format")
        void loginInvalidEmail() throws Exception {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"notanemail\", \"password\":\"Password1\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when password is blank")
        void loginBlankPassword() throws Exception {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\", \"password\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterEndpoint {

        @Test
        @DisplayName("201 with valid registration")
        void registerSuccess() throws Exception {
            RegisterRequest req = new RegisterRequest("Jane Doe", "Password1", "jane@example.com", "+977-9800000001", Role.CUSTOMER, "Kathmandu");
            LoginResponse resp = new LoginResponse("new-token", "jane@example.com", "Jane Doe", "CUSTOMER", 2L);
            when(authService.register(any(RegisterRequest.class))).thenReturn(resp);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").value("new-token"))
                    .andExpect(jsonPath("$.name").value("Jane Doe"));
        }

        @Test
        @DisplayName("400 when name is blank")
        void registerBlankName() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"password\":\"Password1\",\"email\":\"j@x.com\",\"role\":\"CUSTOMER\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when password too short")
        void registerPasswordTooShort() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Jane\",\"password\":\"Ab1\",\"email\":\"j@x.com\",\"role\":\"CUSTOMER\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when password lacks uppercase")
        void registerPasswordNoUppercase() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Jane\",\"password\":\"password1\",\"email\":\"j@x.com\",\"role\":\"CUSTOMER\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when password lacks digit")
        void registerPasswordNoDigit() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Jane\",\"password\":\"Password\",\"email\":\"j@x.com\",\"role\":\"CUSTOMER\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when role is null")
        void registerNullRole() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Jane\",\"password\":\"Password1\",\"email\":\"j@x.com\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 when email is missing")
        void registerNoEmail() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Jane\",\"password\":\"Password1\",\"role\":\"CUSTOMER\"}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
