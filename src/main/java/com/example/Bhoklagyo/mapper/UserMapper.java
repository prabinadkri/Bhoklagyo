package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.UserResponse;
import com.example.Bhoklagyo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole(),
            user.getAddress()
        );
    }
    public java.util.List<UserResponse> toResponseList(java.util.List<User> users) {
        java.util.List<UserResponse> responses = new java.util.ArrayList<>();
        for (User user : users) {
            responses.add(toResponse(user));
        }
        return responses;
    }
}
