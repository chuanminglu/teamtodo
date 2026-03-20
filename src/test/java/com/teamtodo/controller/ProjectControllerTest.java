package com.teamtodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.CreateProjectRequest;
import com.teamtodo.dto.ProjectResponse;
import com.teamtodo.service.ProjectService;
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
 * Controller tests for ProjectController following TDD
 * Covers T004-02, T004-03, T004-04
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    /**
     * T004-02 / AC1: Test create project API endpoint - success
     */
    @Test
    void testCreateProject_Success() throws Exception {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        request.setDescription("Great project");
        request.setOwnerId(1L);

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setName("My Project");
        response.setDescription("Great project");
        response.setOwnerId(1L);
        response.setCreatedAt(LocalDateTime.now());

        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Project"))
                .andExpect(jsonPath("$.description").value("Great project"))
                .andExpect(jsonPath("$.ownerId").value(1));

        verify(projectService, times(1)).createProject(any(CreateProjectRequest.class));
    }

    /**
     * T004-02 / AC4: Test create project with missing name returns 400
     */
    @Test
    void testCreateProject_MissingName_Returns400() throws Exception {
        // Arrange - name is missing (required field)
        CreateProjectRequest request = new CreateProjectRequest();
        request.setOwnerId(1L);
        // name is blank

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any());
    }

    /**
     * T004-02 / AC4: Test create project with description optional (no description is OK)
     */
    @Test
    void testCreateProject_NoDescription_Success() throws Exception {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        // description is intentionally omitted
        request.setOwnerId(1L);

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setName("My Project");
        response.setOwnerId(1L);

        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("My Project"));
    }

    /**
     * T004-02: Test create project with user not found returns 400
     */
    @Test
    void testCreateProject_UserNotFound_Returns400() throws Exception {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        request.setOwnerId(999L);

        when(projectService.createProject(any(CreateProjectRequest.class)))
                .thenThrow(new IllegalArgumentException("User not found with id: 999"));

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found with id: 999"));
    }

    /**
     * T004-03 / AC5: Test list projects returns sorted list
     */
    @Test
    void testListProjects_Success() throws Exception {
        // Arrange
        ProjectResponse p1 = new ProjectResponse();
        p1.setId(2L);
        p1.setName("Newer Project");
        p1.setOwnerId(1L);
        p1.setCreatedAt(LocalDateTime.now());

        ProjectResponse p2 = new ProjectResponse();
        p2.setId(1L);
        p2.setName("Older Project");
        p2.setOwnerId(1L);
        p2.setCreatedAt(LocalDateTime.now().minusDays(1));

        List<ProjectResponse> projects = Arrays.asList(p1, p2);
        when(projectService.listProjects()).thenReturn(projects);

        // Act & Assert
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Newer Project"))
                .andExpect(jsonPath("$[1].name").value("Older Project"));

        verify(projectService, times(1)).listProjects();
    }

    /**
     * T004-03: Test list projects returns empty list when no projects exist
     */
    @Test
    void testListProjects_EmptyList() throws Exception {
        // Arrange
        when(projectService.listProjects()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * T004-04: Test get project detail by ID - success
     */
    @Test
    void testGetProject_Success() throws Exception {
        // Arrange
        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setName("Test Project");
        response.setDescription("Description");
        response.setOwnerId(1L);

        when(projectService.getProject(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Description"));

        verify(projectService, times(1)).getProject(1L);
    }

    /**
     * T004-04: Test get project with non-existent ID returns 400
     */
    @Test
    void testGetProject_NotFound_Returns400() throws Exception {
        // Arrange
        when(projectService.getProject(999L))
                .thenThrow(new IllegalArgumentException("Project not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/projects/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Project not found with id: 999"));
    }
}
