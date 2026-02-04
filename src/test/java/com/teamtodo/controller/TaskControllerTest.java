package com.teamtodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.CreateTaskRequest;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.exception.ResourceNotFoundException;
import com.teamtodo.exception.UnauthorizedException;
import com.teamtodo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for TaskController
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private CreateTaskRequest request;
    private TaskResponse response;

    @BeforeEach
    void setUp() {
        request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority("high");
        request.setProjectId(1L);
        request.setAssigneeId(2L);

        response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Test Task");
        response.setDescription("Test Description");
        response.setPriority("high");
        response.setStatus("todo");
        response.setProjectId(1L);
        response.setCreatorId(1L);
        response.setAssigneeId(2L);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTask_Success() throws Exception {
        // Given
        when(taskService.createTask(any(CreateTaskRequest.class), eq(1L))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.priority").value("high"))
                .andExpect(jsonPath("$.status").value("todo"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.creatorId").value(1))
                .andExpect(jsonPath("$.assigneeId").value(2));
    }

    @Test
    void testCreateTask_MissingTitle() throws Exception {
        // Given
        request.setTitle(null);

        // When & Then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTask_MissingProjectId() throws Exception {
        // Given
        request.setProjectId(null);

        // When & Then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTask_ProjectNotFound() throws Exception {
        // Given
        when(taskService.createTask(any(CreateTaskRequest.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // When & Then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTask_Unauthorized() throws Exception {
        // Given
        when(taskService.createTask(any(CreateTaskRequest.class), eq(1L)))
                .thenThrow(new UnauthorizedException("User is not a member of the project"));

        // When & Then
        mockMvc.perform(post("/tasks")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        // Given
        when(taskService.getTaskById(1L))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }
}
