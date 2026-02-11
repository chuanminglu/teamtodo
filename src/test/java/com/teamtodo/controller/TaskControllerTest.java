package com.teamtodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.UpdateTaskRequest;
import com.teamtodo.entity.Task;
import com.teamtodo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for TaskController following TDD
 * Tests for US010: Task Edit Feature - Controller Layer
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TaskService taskService;
    
    private Task testTask;
    private UpdateTaskRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setProjectId(1L);
        testTask.setTitle("Updated Title");
        testTask.setDescription("Updated Description");
        testTask.setPriority("HIGH");
        testTask.setDeadline(LocalDateTime.of(2026, 4, 1, 12, 0));
        testTask.setCreatorId(100L);
        testTask.setAssigneeId(200L);
        testTask.setCreatedAt(LocalDateTime.now().minusDays(1));
        testTask.setUpdatedAt(LocalDateTime.now());
        
        updateRequest = new UpdateTaskRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriority("HIGH");
        updateRequest.setDeadline(LocalDateTime.of(2026, 4, 1, 12, 0));
    }
    
    @Test
    void testUpdateTask_Success() throws Exception {
        // Given: Valid update request
        Long taskId = 1L;
        Long userId = 100L;
        
        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequest.class), eq(userId)))
            .thenReturn(testTask);
        
        // When & Then: Call API
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.priority").value("HIGH"))
            .andExpect(jsonPath("$.creatorId").value(100))
            .andExpect(jsonPath("$.assigneeId").value(200));
    }
    
    @Test
    void testUpdateTask_ValidationError_TitleTooLong() throws Exception {
        // Given: Invalid request - title too long
        Long taskId = 1L;
        Long userId = 100L;
        
        updateRequest.setTitle("a".repeat(201)); // Exceeds max length
        
        // When & Then: Call API
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testUpdateTask_TaskNotFound() throws Exception {
        // Given: Task does not exist
        Long taskId = 999L;
        Long userId = 100L;
        
        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequest.class), eq(userId)))
            .thenThrow(new IllegalArgumentException("Task not found"));
        
        // When & Then: Call API
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Task not found"));
    }
    
    @Test
    void testUpdateTask_PermissionDenied() throws Exception {
        // Given: User lacks permission
        Long taskId = 1L;
        Long userId = 999L;
        
        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequest.class), eq(userId)))
            .thenThrow(new IllegalStateException("Only task creator or assignee can edit the task"));
        
        // When & Then: Call API
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Only task creator or assignee can edit the task"));
    }
    
    @Test
    void testUpdateTask_MissingUserId() throws Exception {
        // Given: userId parameter is missing
        Long taskId = 1L;
        
        // When & Then: Call API without userId
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest());
    }
}
