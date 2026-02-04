package com.teamtodo.service;

import com.teamtodo.dto.CreateTaskRequest;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.entity.Project;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.entity.Task;
import com.teamtodo.entity.User;
import com.teamtodo.exception.ResourceNotFoundException;
import com.teamtodo.exception.UnauthorizedException;
import com.teamtodo.mapper.ProjectMapper;
import com.teamtodo.mapper.ProjectMemberMapper;
import com.teamtodo.mapper.TaskMapper;
import com.teamtodo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for TaskService
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectMemberMapper projectMemberMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private CreateTaskRequest request;
    private User creator;
    private Project project;
    private ProjectMember projectMember;

    @BeforeEach
    void setUp() {
        request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority("high");
        request.setProjectId(1L);
        request.setAssigneeId(2L);

        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        projectMember = new ProjectMember();
        projectMember.setId(1L);
        projectMember.setProjectId(1L);
        projectMember.setUserId(1L);
        projectMember.setRole("member");
    }

    @Test
    void testCreateTask_Success() {
        // Given
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectMemberMapper.selectOne(any())).thenReturn(projectMember);
        when(taskMapper.insert(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(1L);
            return 1;
        });

        // When
        TaskResponse response = taskService.createTask(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("high", response.getPriority());
        assertEquals("todo", response.getStatus());
        assertEquals(1L, response.getProjectId());
        assertEquals(1L, response.getCreatorId());
        assertEquals(2L, response.getAssigneeId());
        
        verify(taskMapper, times(1)).insert(any(Task.class));
    }

    @Test
    void testCreateTask_ProjectNotFound() {
        // Given
        when(projectMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(request, 1L);
        });
        
        verify(taskMapper, never()).insert(any(Task.class));
    }

    @Test
    void testCreateTask_UserNotProjectMember() {
        // Given
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectMemberMapper.selectOne(any())).thenReturn(null);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.createTask(request, 1L);
        });
        
        verify(taskMapper, never()).insert(any(Task.class));
    }

    @Test
    void testCreateTask_WithoutOptionalFields() {
        // Given
        request.setDescription(null);
        request.setAssigneeId(null);
        request.setDueDate(null);
        
        when(projectMapper.selectById(1L)).thenReturn(project);
        when(projectMemberMapper.selectOne(any())).thenReturn(projectMember);
        when(taskMapper.insert(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(1L);
            return 1;
        });

        // When
        TaskResponse response = taskService.createTask(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertNull(response.getDescription());
        assertNull(response.getAssigneeId());
        assertNull(response.getDueDate());
    }

    @Test
    void testGetTaskById_Success() {
        // Given
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setProjectId(1L);
        task.setCreatorId(1L);
        task.setStatus("todo");
        
        when(taskMapper.selectById(1L)).thenReturn(task);

        // When
        TaskResponse response = taskService.getTaskById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void testGetTaskById_NotFound() {
        // Given
        when(taskMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(1L);
        });
    }
}
