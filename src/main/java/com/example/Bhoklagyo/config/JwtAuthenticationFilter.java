package com.example.Bhoklagyo.config;

import com.example.Bhoklagyo.service.CustomUserDetailsService;
import com.example.Bhoklagyo.security.AdminUserDetailsService;
import com.example.Bhoklagyo.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
                                   AdminUserDetailsService adminUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = null;
                    
                    // Load user details based on role
                    if ("ADMIN".equals(role)) {
                        try {
                            userDetails = adminUserDetailsService.loadUserByUsername(username);
                        } catch (UsernameNotFoundException e) {
                            // Admin not found, continue without authentication
                        }
                    } else {
                        try {
                            userDetails = userDetailsService.loadUserByUsername(username);
                        } catch (UsernameNotFoundException e) {
                            // User not found, continue without authentication
                        }
                    }
                    
                    if (userDetails != null && jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Token validation failed, continue without authentication
                logger.error("JWT validation failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
