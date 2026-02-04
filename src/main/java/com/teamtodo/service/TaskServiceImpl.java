package com.teamtodo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamtodo.dto.CreateTaskRequest;
import com.teamtodo.dto.TaskResponse;
import com.teamtodo.entity.Project;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.entity.Task;
import com.teamtodo.exception.ResourceNotFoundException;
import com.teamtodo.exception.UnauthorizedException;
import com.teamtodo.mapper.ProjectMapper;
import com.teamtodo.mapper.ProjectMemberMapper;
import com.teamtodo.mapper.TaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of TaskService
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, Long userId) {
        // Validate that project exists
        Project project = projectMapper.selectById(request.getProjectId());
        if (project == null) {
            throw new ResourceNotFoundException("Project not found with id: " + request.getProjectId());
        }

        // Check if user is a member of the project (AC5: Non-project members cannot create tasks)
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", request.getProjectId());
        queryWrapper.eq("user_id", userId);
        ProjectMember projectMember = projectMemberMapper.selectOne(queryWrapper);
        
        if (projectMember == null) {
            throw new UnauthorizedException("User is not a member of the project");
        }

        // Create task entity
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "medium");
        task.setStatus("todo"); // Default status (AC3: Task appears in todo column)
        task.setProjectId(request.getProjectId());
        task.setCreatorId(userId); // AC4: Creator automatically becomes the task creator
        task.setAssigneeId(request.getAssigneeId());
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // Save task
        taskMapper.insert(task);

        // Convert to response
        return convertToResponse(task);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return convertToResponse(task);
    }

    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        BeanUtils.copyProperties(task, response);
        return response;
    }
}
