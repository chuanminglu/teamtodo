package com.teamtodo.controller;

import com.teamtodo.dto.UpdateTaskRequest;
import com.teamtodo.entity.Task;
import com.teamtodo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Task Controller
 * REST API for US010: Task Edit Feature
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    /**
     * Update task information
     * AC2: Editable fields - title, description, priority, deadline
     * AC4: Only creator or assignee can edit
     * 
     * @param id Task ID
     * @param request Update request
     * @param userId User requesting the update
     * @return Updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            @RequestParam Long userId) {
        try {
            Task updatedTask = taskService.updateTask(id, request, userId);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(404).body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(404).body(error);
    }
}
