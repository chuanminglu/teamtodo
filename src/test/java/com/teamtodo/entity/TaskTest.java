package com.teamtodo.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Task entity
 */
class TaskTest {

    @Test
    void testTaskCreation() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority("high");
        task.setStatus("todo");
        task.setProjectId(1L);
        task.setCreatorId(1L);
        task.setAssigneeId(2L);
        
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        assertEquals(1L, task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals("high", task.getPriority());
        assertEquals("todo", task.getStatus());
        assertEquals(1L, task.getProjectId());
        assertEquals(1L, task.getCreatorId());
        assertEquals(2L, task.getAssigneeId());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    void testTaskWithOnlyRequiredFields() {
        Task task = new Task();
        task.setTitle("Required Task");
        task.setProjectId(1L);
        task.setCreatorId(1L);

        assertEquals("Required Task", task.getTitle());
        assertEquals(1L, task.getProjectId());
        assertEquals(1L, task.getCreatorId());
        assertNull(task.getDescription());
        assertNull(task.getAssigneeId());
        assertNull(task.getDueDate());
    }
}
