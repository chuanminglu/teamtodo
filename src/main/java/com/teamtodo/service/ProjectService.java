package com.teamtodo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamtodo.dto.CreateProjectRequest;
import com.teamtodo.dto.ProjectResponse;
import com.teamtodo.entity.Project;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.mapper.ProjectMapper;
import com.teamtodo.mapper.ProjectMemberMapper;
import com.teamtodo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing projects
 * Implements T004-01 to T004-04
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * T004-02: Create a new project
     * AC1: Admin fills in project name and description
     * AC3: Creator automatically becomes project Owner
     * AC4: Project name is required
     */
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        // Validate owner exists
        if (userMapper.selectById(request.getOwnerId()) == null) {
            throw new IllegalArgumentException("User not found with id: " + request.getOwnerId());
        }

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwnerId(request.getOwnerId());
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        projectMapper.insert(project);

        // AC3: Creator automatically becomes project Owner member
        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProjectId(project.getId());
        ownerMember.setUserId(request.getOwnerId());
        ownerMember.setRole("OWNER");
        ownerMember.setJoinedAt(LocalDateTime.now());
        projectMemberMapper.insert(ownerMember);

        return toResponse(project);
    }

    /**
     * T004-03: Get list of all projects
     * AC5: Project list ordered by createdAt descending
     */
    public List<ProjectResponse> listProjects() {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        List<Project> projects = projectMapper.selectList(queryWrapper);
        return projects.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * T004-04: Get project detail by ID
     */
    public ProjectResponse getProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new IllegalArgumentException("Project not found with id: " + id);
        }
        return toResponse(project);
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setOwnerId(project.getOwnerId());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }
}
