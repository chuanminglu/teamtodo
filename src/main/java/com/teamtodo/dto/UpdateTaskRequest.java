package com.teamtodo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for updating task
 * Used in US010: Task Edit Feature
 */
@Data
public class UpdateTaskRequest {
    
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    private String description;
    
    private String priority;
    
    private LocalDateTime deadline;
}
