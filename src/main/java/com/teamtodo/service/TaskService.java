package com.teamtodo.service;

import com.teamtodo.dto.CreateTaskRequest;
import com.teamtodo.dto.TaskResponse;

/**
 * Service interface for Task operations
 */
public interface TaskService {
    
    /**
     * Create a new task
     * @param request the task creation request
     * @param userId the ID of the user creating the task
     * @return the created task response
     */
    TaskResponse createTask(CreateTaskRequest request, Long userId);
    
    /**
     * Get a task by its ID
     * @param taskId the task ID
     * @return the task response
     */
    TaskResponse getTaskById(Long taskId);
}
