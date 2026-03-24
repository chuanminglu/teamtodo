package com.teamtodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.dto.UpdateTaskStatusRequest;
import com.teamtodo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for TaskController – US015: manual task status change
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    /**
     * T015-01 / AC3: PATCH status returns updated task
     */
    @Test
    void testUpdateTaskStatus_Success() throws Exception {
        // Arrange
        TaskResponse response = buildTaskResponse(10L, 1L, "IN_PROGRESS");
        when(taskService.updateTaskStatus(eq(10L), any(UpdateTaskStatusRequest.class)))
                .thenReturn(response);

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(1L);
        request.setStatus("IN_PROGRESS");

        // Act & Assert
        mockMvc.perform(patch("/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskService, times(1)).updateTaskStatus(eq(10L), any(UpdateTaskStatusRequest.class));
    }

    /**
     * AC4: Unauthorized user gets 403
     */
    @Test
    void testUpdateTaskStatus_Unauthorized() throws Exception {
        // Arrange
        when(taskService.updateTaskStatus(eq(10L), any(UpdateTaskStatusRequest.class)))
                .thenThrow(new IllegalArgumentException("No permission to change task status"));

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(99L);
        request.setStatus("IN_PROGRESS");

        // Act & Assert
        mockMvc.perform(patch("/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No permission to change task status"));
    }

    /**
     * Task not found returns 404
     */
    @Test
    void testUpdateTaskStatus_TaskNotFound() throws Exception {
        // Arrange
        when(taskService.updateTaskStatus(eq(999L), any(UpdateTaskStatusRequest.class)))
                .thenThrow(new IllegalArgumentException("Task not found with id: 999"));

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(1L);
        request.setStatus("IN_PROGRESS");

        // Act & Assert
        mockMvc.perform(patch("/tasks/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Validation error when request body is missing required fields
     */
    @Test
    void testUpdateTaskStatus_ValidationError() throws Exception {
        // Missing requestUserId and status
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();

        mockMvc.perform(patch("/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTaskStatus(any(), any());
    }

    /**
     * AC3: GET tasks by project returns list
     */
    @Test
    void testGetTasksByProject_Success() throws Exception {
        // Arrange
        List<TaskResponse> tasks = Arrays.asList(
                buildTaskResponse(10L, 1L, "TODO"),
                buildTaskResponse(11L, 1L, "DONE")
        );
        when(taskService.getTasksByProject(1L)).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].status").value("DONE"));

        verify(taskService, times(1)).getTasksByProject(1L);
    }

    // ---- helpers ----

    private TaskResponse buildTaskResponse(Long id, Long projectId, String status) {
        TaskResponse r = new TaskResponse();
        r.setId(id);
        r.setProjectId(projectId);
        r.setTitle("Task " + id);
        r.setDescription("Description");
        r.setStatus(status);
        r.setCreatorId(1L);
        r.setAssigneeId(2L);
        r.setAssigneeUsername("assignee");
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }
}
