package com.teamtodo.controller;

import com.teamtodo.dto.TaskResponse;
import com.teamtodo.dto.UpdateTaskStatusRequest;
import com.teamtodo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Task management – US015: manual task status change
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * T015-01: Update task status
     * AC1/AC2: Status switch button triggers this endpoint
     * AC3: Returns updated task so the board can refresh
     * AC4: Permission check is inside TaskService
     *
     * @param taskId  ID of the task
     * @param request requestUserId + new status
     * @return updated TaskResponse or error
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        try {
            TaskResponse updated = taskService.updateTaskStatus(taskId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * AC3: Get all tasks for a project (kanban board data)
     *
     * @param projectId project ID
     * @return list of tasks
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }
}
