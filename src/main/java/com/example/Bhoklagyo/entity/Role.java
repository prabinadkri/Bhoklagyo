package com.example.Bhoklagyo.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

public enum Role {
    CUSTOMER,
    OWNER,
    EMPLOYEE;

    @JsonCreator
    public static Role from(Object value) {
        if (value == null) return null;

        // If client sent a plain string: "CUSTOMER"
        if (value instanceof String) {
            String s = ((String) value).trim();
            try {
                return Role.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role value: " + s);
            }
        }

        // If client sent an object: { "name": "CUSTOMER" }
        if (value instanceof Map) {
            Object name = ((Map<?, ?>) value).get("name");
            if (name instanceof String) {
                String s = ((String) name).trim();
                try {
                    return Role.valueOf(s.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid role value in object: " + s);
                }
            }
        }

        throw new IllegalArgumentException("Cannot deserialize Role from value: " + value);
    }
}
