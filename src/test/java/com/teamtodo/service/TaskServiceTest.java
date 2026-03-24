package com.teamtodo.service;

import com.teamtodo.dto.TaskResponse;
import com.teamtodo.dto.UpdateTaskStatusRequest;
import com.teamtodo.entity.Task;
import com.teamtodo.entity.User;
import com.teamtodo.mapper.TaskMapper;
import com.teamtodo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TDD tests for TaskService – US015: manual task status change
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User creator;
    private User assignee;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");
        creator.setEmail("creator@test.com");

        assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("assignee");
        assignee.setEmail("assignee@test.com");

        task = new Task();
        task.setId(10L);
        task.setProjectId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus("TODO");
        task.setCreatorId(1L);
        task.setAssigneeId(2L);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * T015-01 / AC3: Creator can update task status successfully
     */
    @Test
    void testUpdateTaskStatus_ByCreator_Success() {
        // Arrange
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(1L); // creator
        request.setStatus("IN_PROGRESS");

        when(taskMapper.selectById(10L)).thenReturn(task);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        when(userMapper.selectById(2L)).thenReturn(assignee);

        // Act
        TaskResponse result = taskService.updateTaskStatus(10L, request);

        // Assert
        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(taskMapper, times(1)).updateById(any(Task.class));
    }

    /**
     * AC3: Assignee can update task status successfully
     */
    @Test
    void testUpdateTaskStatus_ByAssignee_Success() {
        // Arrange
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(2L); // assignee
        request.setStatus("DONE");

        when(taskMapper.selectById(10L)).thenReturn(task);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        when(userMapper.selectById(2L)).thenReturn(assignee);

        // Act
        TaskResponse result = taskService.updateTaskStatus(10L, request);

        // Assert
        assertNotNull(result);
        assertEquals("DONE", result.getStatus());
    }

    /**
     * AC4: User who is neither creator nor assignee cannot change status
     */
    @Test
    void testUpdateTaskStatus_UnauthorizedUser_ThrowsException() {
        // Arrange
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(99L); // neither creator nor assignee
        request.setStatus("IN_PROGRESS");

        when(taskMapper.selectById(10L)).thenReturn(task);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTaskStatus(10L, request));

        assertTrue(exception.getMessage().contains("permission"));
        verify(taskMapper, never()).updateById(any(Task.class));
    }

    /**
     * AC2: Invalid status value is rejected
     */
    @Test
    void testUpdateTaskStatus_InvalidStatus_ThrowsException() {
        // Arrange
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(1L); // creator
        request.setStatus("INVALID_STATUS");

        when(taskMapper.selectById(10L)).thenReturn(task);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTaskStatus(10L, request));

        assertTrue(exception.getMessage().contains("Invalid status"));
        verify(taskMapper, never()).updateById(any(Task.class));
    }

    /**
     * Task not found returns exception
     */
    @Test
    void testUpdateTaskStatus_TaskNotFound_ThrowsException() {
        // Arrange
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(1L);
        request.setStatus("IN_PROGRESS");

        when(taskMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTaskStatus(999L, request));

        assertTrue(exception.getMessage().contains("Task not found"));
        verify(taskMapper, never()).updateById(any(Task.class));
    }

    /**
     * AC3: getTasksByProject returns tasks for a project
     */
    @Test
    void testGetTasksByProject_Success() {
        // Arrange
        Task task2 = new Task();
        task2.setId(11L);
        task2.setProjectId(1L);
        task2.setTitle("Another Task");
        task2.setStatus("DONE");
        task2.setCreatorId(1L);
        task2.setAssigneeId(null);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());

        when(taskMapper.selectList(any())).thenReturn(Arrays.asList(task, task2));
        when(userMapper.selectById(2L)).thenReturn(assignee);

        // Act
        List<TaskResponse> result = taskService.getTasksByProject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        assertEquals("TODO", result.get(0).getStatus());
        assertEquals("assignee", result.get(0).getAssigneeUsername());
    }

    /**
     * getTasksByProject returns empty list when no tasks
     */
    @Test
    void testGetTasksByProject_Empty() {
        // Arrange
        when(taskMapper.selectList(any())).thenReturn(List.of());

        // Act
        List<TaskResponse> result = taskService.getTasksByProject(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Status update to all three valid statuses: TODO, IN_PROGRESS, DONE
     */
    @Test
    void testUpdateTaskStatus_AllValidStatuses() {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setRequestUserId(1L);
        when(userMapper.selectById(2L)).thenReturn(assignee);

        for (String status : List.of("TODO", "IN_PROGRESS", "DONE")) {
            task.setStatus("TODO"); // reset
            request.setStatus(status);
            when(taskMapper.selectById(10L)).thenReturn(task);
            when(taskMapper.updateById(any(Task.class))).thenReturn(1);

            TaskResponse result = taskService.updateTaskStatus(10L, request);
            assertEquals(status, result.getStatus());
        }
    }
}
