package com.teamtodo.service;

import com.teamtodo.dto.UpdateTaskRequest;
import com.teamtodo.entity.Task;
import com.teamtodo.mapper.TaskMapper;
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
 * Test class for TaskService following TDD
 * Tests for US010: Task Edit Feature
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskMapper taskMapper;
    
    @InjectMocks
    private TaskService taskService;
    
    private Task testTask;
    private UpdateTaskRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setProjectId(1L);
        testTask.setTitle("Original Title");
        testTask.setDescription("Original Description");
        testTask.setPriority("MEDIUM");
        testTask.setDeadline(LocalDateTime.of(2026, 3, 1, 12, 0));
        testTask.setCreatorId(100L);
        testTask.setAssigneeId(200L);
        testTask.setCreatedAt(LocalDateTime.now().minusDays(1));
        testTask.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        updateRequest = new UpdateTaskRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriority("HIGH");
        updateRequest.setDeadline(LocalDateTime.of(2026, 4, 1, 12, 0));
    }
    
    @Test
    void testUpdateTask_Success_AsCreator() {
        // Given: Task exists and user is creator
        Long taskId = 1L;
        Long userId = 100L; // Same as creator_id
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setPriority("HIGH");
        updatedTask.setDeadline(LocalDateTime.of(2026, 4, 1, 12, 0));
        
        when(taskMapper.selectById(taskId)).thenReturn(testTask, updatedTask);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        
        // When: Update task
        Task result = taskService.updateTask(taskId, updateRequest, userId);
        
        // Then: Task should be updated successfully
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("HIGH", result.getPriority());
        assertEquals(LocalDateTime.of(2026, 4, 1, 12, 0), result.getDeadline());
        
        verify(taskMapper, times(2)).selectById(taskId);
        verify(taskMapper).updateById(any(Task.class));
    }
    
    @Test
    void testUpdateTask_Success_AsAssignee() {
        // Given: Task exists and user is assignee
        Long taskId = 1L;
        Long userId = 200L; // Same as assignee_id
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Title");
        
        when(taskMapper.selectById(taskId)).thenReturn(testTask, updatedTask);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        
        // When: Update task
        Task result = taskService.updateTask(taskId, updateRequest, userId);
        
        // Then: Task should be updated successfully
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        
        verify(taskMapper, times(2)).selectById(taskId);
        verify(taskMapper).updateById(any(Task.class));
    }
    
    @Test
    void testUpdateTask_TaskNotFound() {
        // Given: Task does not exist
        Long taskId = 999L;
        Long userId = 100L;
        
        when(taskMapper.selectById(taskId)).thenReturn(null);
        
        // When & Then: Should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.updateTask(taskId, updateRequest, userId)
        );
        
        assertEquals("Task not found", exception.getMessage());
        verify(taskMapper).selectById(taskId);
        verify(taskMapper, never()).updateById(any(Task.class));
    }
    
    @Test
    void testUpdateTask_PermissionDenied() {
        // Given: Task exists but user is neither creator nor assignee
        Long taskId = 1L;
        Long userId = 999L; // Different from creator and assignee
        
        when(taskMapper.selectById(taskId)).thenReturn(testTask);
        
        // When & Then: Should throw exception
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> taskService.updateTask(taskId, updateRequest, userId)
        );
        
        assertEquals("Only task creator or assignee can edit the task", exception.getMessage());
        verify(taskMapper).selectById(taskId);
        verify(taskMapper, never()).updateById(any(Task.class));
    }
    
    @Test
    void testUpdateTask_PartialUpdate() {
        // Given: Task exists and only some fields are updated
        Long taskId = 1L;
        Long userId = 100L;
        
        UpdateTaskRequest partialRequest = new UpdateTaskRequest();
        partialRequest.setTitle("New Title Only");
        // Other fields are null, should keep original values
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("New Title Only");
        updatedTask.setDescription("Original Description");
        updatedTask.setPriority("MEDIUM");
        
        when(taskMapper.selectById(taskId)).thenReturn(testTask, updatedTask);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        
        // When: Update task with partial data
        Task result = taskService.updateTask(taskId, partialRequest, userId);
        
        // Then: Only title should be updated, others kept
        assertNotNull(result);
        assertEquals("New Title Only", result.getTitle());
        assertEquals("Original Description", result.getDescription()); // Unchanged
        assertEquals("MEDIUM", result.getPriority()); // Unchanged
        
        verify(taskMapper, times(2)).selectById(taskId);
        verify(taskMapper).updateById(any(Task.class));
    }
    
    @Test
    void testUpdateTask_NoAssignee() {
        // Given: Task has no assignee (assigneeId is null)
        Long taskId = 1L;
        Long userId = 100L; // Creator
        
        testTask.setAssigneeId(null);
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Title");
        
        when(taskMapper.selectById(taskId)).thenReturn(testTask, updatedTask);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        
        // When: Creator updates task
        Task result = taskService.updateTask(taskId, updateRequest, userId);
        
        // Then: Should succeed
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        
        verify(taskMapper, times(2)).selectById(taskId);
        verify(taskMapper).updateById(any(Task.class));
    }
}
