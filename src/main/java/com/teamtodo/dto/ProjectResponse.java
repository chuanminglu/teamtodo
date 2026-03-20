package com.teamtodo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for project information response
 */
@Data
public class ProjectResponse {

    private Long id;

    private String name;

    private String description;

    private Long ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
