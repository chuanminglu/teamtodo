package com.teamtodo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating task status (US015)
 */
public class UpdateTaskStatusRequest {

    @NotNull(message = "requestUserId is required")
    private Long requestUserId;

    @NotBlank(message = "status is required")
    private String status;

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
