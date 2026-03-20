package com.teamtodo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new project
 * AC4: Project name is required, description is optional
 */
@Data
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
