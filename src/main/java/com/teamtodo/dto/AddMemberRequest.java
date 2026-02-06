package com.teamtodo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for adding a member to a project
 */
@Data
public class AddMemberRequest {
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String role = "MEMBER"; // Default role
}
