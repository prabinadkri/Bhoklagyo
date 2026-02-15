package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.Role;
import jakarta.validation.constraints.NotNull;

public class RoleUpdateRequest {
    @NotNull(message = "Role is required")
    private Role newRole;

    public RoleUpdateRequest() {}

    public RoleUpdateRequest(Role newRole) {
        this.newRole = newRole;
    }

    public Role getNewRole() {
        return newRole;
    }

    public void setNewRole(Role newRole) {
        this.newRole = newRole;
    }
}
