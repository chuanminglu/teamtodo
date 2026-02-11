package com.teamtodo.service;

import com.teamtodo.dto.UpdateTaskRequest;
import com.teamtodo.entity.Task;
import com.teamtodo.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Task Service
 * Implements US010: Task Edit Feature
 */
@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskMapper taskMapper;
    
    /**
     * Update task information
     * AC4: Only task creator or assignee can edit
     * 
     * @param taskId Task ID
     * @param request Update request with fields to update
     * @param userId User requesting the update
     * @return Updated task
     * @throws IllegalArgumentException if task not found
     * @throws IllegalStateException if user lacks permission
     */
    @Transactional
    public Task updateTask(Long taskId, UpdateTaskRequest request, Long userId) {
        // Get existing task
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        
        // AC4: Check permission - only creator or assignee can edit
        boolean isCreator = userId.equals(task.getCreatorId());
        boolean isAssignee = task.getAssigneeId() != null && userId.equals(task.getAssigneeId());
        
        if (!isCreator && !isAssignee) {
            throw new IllegalStateException("Only task creator or assignee can edit the task");
        }
        
        // Update only provided fields (partial update)
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDeadline() != null) {
            task.setDeadline(request.getDeadline());
        }
        
        // Update the task (updatedAt will be set by database trigger)
        taskMapper.updateById(task);
        
        // Return updated task
        return taskMapper.selectById(taskId);
    }
}
