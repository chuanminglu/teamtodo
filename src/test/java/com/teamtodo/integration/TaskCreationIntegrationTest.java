package com.teamtodo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.CreateTaskRequest;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.entity.Project;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.entity.User;
import com.teamtodo.mapper.ProjectMapper;
import com.teamtodo.mapper.ProjectMemberMapper;
import com.teamtodo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for Task Creation feature (US008)
 * Tests all acceptance criteria with real database
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskCreationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    private User testUser;
    private User testUser2;
    private Project testProject;

    @BeforeEach
    void setUp() {
        // Clean up and create test data
        // Create test users
        testUser = new User();
        testUser.setUsername("testuser1");
        testUser.setEmail("test1@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(testUser);

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(testUser2);

        // Create test project
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setCreatedAt(LocalDateTime.now());
        testProject.setUpdatedAt(LocalDateTime.now());
        projectMapper.insert(testProject);

        // Add testUser as project member
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(testProject.getId());
        projectMember.setUserId(testUser.getId());
        projectMember.setRole("member");
        projectMember.setCreatedAt(LocalDateTime.now());
        projectMemberMapper.insert(projectMember);
    }

    @Test
    void testAC1_CreateTaskEndpointAvailable() throws Exception {
        // AC1: Task creation endpoint available
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setProjectId(testProject.getId());

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAC2_RequiredAndOptionalFields() throws Exception {
        // AC2: Required field (title) and optional fields
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Required Title Task");
        request.setDescription("Optional Description");
        request.setPriority("high");
        request.setProjectId(testProject.getId());
        request.setAssigneeId(testUser2.getId());
        request.setDueDate(LocalDateTime.now().plusDays(7));

        MvcResult result = mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Required Title Task"))
                .andExpect(jsonPath("$.description").value("Optional Description"))
                .andExpect(jsonPath("$.priority").value("high"))
                .andExpect(jsonPath("$.assigneeId").value(testUser2.getId()))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TaskResponse response = objectMapper.readValue(responseJson, TaskResponse.class);
        assertNotNull(response.getId());
        assertNotNull(response.getDueDate());
    }

    @Test
    void testAC2_OnlyRequiredFields() throws Exception {
        // AC2: Only required field (title)
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Minimal Task");
        request.setProjectId(testProject.getId());

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Minimal Task"))
                .andExpect(jsonPath("$.description").isEmpty());
    }

    @Test
    void testAC3_TaskAppearsInTodoColumn() throws Exception {
        // AC3: Created task appears in todo column (status = "todo")
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Todo Task");
        request.setProjectId(testProject.getId());

        MvcResult result = mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("todo"))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TaskResponse response = objectMapper.readValue(responseJson, TaskResponse.class);

        // Verify task can be retrieved and still has status "todo"
        mockMvc.perform(get("/tasks/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("todo"));
    }

    @Test
    void testAC4_CreatorAutomaticallySet() throws Exception {
        // AC4: Creator automatically becomes the task creator
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Creator Test Task");
        request.setProjectId(testProject.getId());

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.creatorId").value(testUser.getId()));
    }

    @Test
    void testAC5_NonProjectMemberCannotCreateTask() throws Exception {
        // AC5: Non-project members cannot create tasks
        // testUser2 is not a member of testProject
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Unauthorized Task");
        request.setProjectId(testProject.getId());

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User is not a member of the project"));
    }

    @Test
    void testGetTaskById() throws Exception {
        // Test GET /tasks/{id} endpoint (T008-03)
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Get Task Test");
        request.setProjectId(testProject.getId());

        MvcResult createResult = mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        TaskResponse createResponse = objectMapper.readValue(responseJson, TaskResponse.class);

        // Get the created task
        mockMvc.perform(get("/tasks/" + createResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.getId()))
                .andExpect(jsonPath("$.title").value("Get Task Test"));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        // Test GET /tasks/{id} with non-existent ID
        mockMvc.perform(get("/tasks/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 99999"));
    }

    @Test
    void testCreateTask_ProjectNotFound() throws Exception {
        // Test creating task with non-existent project
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Invalid Project Task");
        request.setProjectId(99999L);

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Project not found with id: 99999"));
    }

    @Test
    void testCreateTask_MissingTitle() throws Exception {
        // Test validation: missing required field
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(testProject.getId());
        // title is missing

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTask_MissingProjectId() throws Exception {
        // Test validation: missing required field
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task without project");
        // projectId is missing

        mockMvc.perform(post("/tasks")
                .header("X-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
