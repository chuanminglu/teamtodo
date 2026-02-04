package com.teamtodo.controller;

import com.teamtodo.dto.CreateTaskRequest;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Task operations
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Create a new task (AC1: Task creation endpoint)
     * @param request the task creation request
     * @param userId the ID of the user (from header, in real app would be from auth token)
     * @return the created task response
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody CreateTaskRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        
        // Validate required fields (AC2: Required fields)
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getProjectId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        TaskResponse response = taskService.createTask(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get task by ID (AC1: Task detail endpoint)
     * @param taskId the task ID
     * @return the task response
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.ok(response);
    }
}
