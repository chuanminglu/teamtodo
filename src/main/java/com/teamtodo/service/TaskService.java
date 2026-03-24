package com.teamtodo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.dto.UpdateTaskStatusRequest;
import com.teamtodo.entity.Task;
import com.teamtodo.entity.User;
import com.teamtodo.mapper.TaskMapper;
import com.teamtodo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for task management – US015: manual task status change
 */
@Service
public class TaskService {

    private static final Set<String> VALID_STATUSES = Set.of("TODO", "IN_PROGRESS", "DONE");

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * T015-01: Update task status with permission check (AC3, AC4)
     *
     * @param taskId  ID of the task to update
     * @param request contains requestUserId and new status
     * @return updated TaskResponse
     */
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        // Validate task exists
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        // AC2: Validate status value
        String newStatus = request.getStatus();
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status: " + newStatus + ". Must be one of: TODO, IN_PROGRESS, DONE");
        }

        // AC4: Only creator or assignee can change status
        Long requestUserId = request.getRequestUserId();
        boolean isCreator = requestUserId.equals(task.getCreatorId());
        boolean isAssignee = task.getAssigneeId() != null && requestUserId.equals(task.getAssigneeId());
        if (!isCreator && !isAssignee) {
            throw new IllegalArgumentException(
                    "No permission to change task status: only the creator or assignee may do so");
        }

        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);

        return toResponse(task);
    }

    /** AC3: Get all tasks for a project (for kanban board refresh)
     * Note: assignee usernames are resolved per-task; this mirrors the existing
     * ProjectMemberService pattern and is acceptable for typical project sizes.
     *
     * @param projectId project ID
     * @return list of TaskResponse
     */
    public List<TaskResponse> getTasksByProject(Long projectId) {
        QueryWrapper<Task> qw = new QueryWrapper<>();
        qw.eq("project_id", projectId);
        List<Task> tasks = taskMapper.selectList(qw);
        return tasks.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ---- helpers ----

    private TaskResponse toResponse(Task task) {
        TaskResponse r = new TaskResponse();
        r.setId(task.getId());
        r.setProjectId(task.getProjectId());
        r.setTitle(task.getTitle());
        r.setDescription(task.getDescription());
        r.setStatus(task.getStatus());
        r.setCreatorId(task.getCreatorId());
        r.setAssigneeId(task.getAssigneeId());
        r.setCreatedAt(task.getCreatedAt());
        r.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssigneeId() != null) {
            User assignee = userMapper.selectById(task.getAssigneeId());
            if (assignee != null) {
                r.setAssigneeUsername(assignee.getUsername());
            }
        }
        return r;
    }
}
