package com.teamtodo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for task information
 */
@Data
public class TaskResponse {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private String priority;
    
    private String status;
    
    private Long projectId;
    
    private Long creatorId;
    
    private Long assigneeId;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
