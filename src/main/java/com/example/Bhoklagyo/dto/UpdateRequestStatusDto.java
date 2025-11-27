package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateRequestStatusDto {
    
    @NotNull(message = "Status is required")
    private RequestStatus status;

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
