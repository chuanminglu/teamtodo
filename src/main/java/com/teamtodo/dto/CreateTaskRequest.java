package com.teamtodo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a task
 */
@Data
public class CreateTaskRequest {
    
    private String title;
    
    private String description;
    
    private String priority;
    
    private Long projectId;
    
    private Long assigneeId;
    
    private LocalDateTime dueDate;
}
