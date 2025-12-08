package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.Role;

public class RoleUpdateRequest {
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
