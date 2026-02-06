package com.teamtodo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamtodo.dto.AddMemberRequest;
import com.teamtodo.dto.MemberResponse;
import com.teamtodo.entity.Project;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.entity.User;
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
 * Service for managing project members
 * Implements T005-01 to T005-04
 */
@Service
public class ProjectMemberService {
    
    @Autowired
    private ProjectMemberMapper projectMemberMapper;
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * T005-02: Add a member to a project with duplicate validation
     */
    @Transactional
    public ProjectMember addMember(AddMemberRequest request) {
        // Validate project exists
        Project project = projectMapper.selectById(request.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("Project not found with id: " + request.getProjectId());
        }
        
        // Validate user exists
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + request.getUserId());
        }
        
        // Check if user is already a member (duplicate validation)
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", request.getProjectId())
                   .eq("user_id", request.getUserId());
        ProjectMember existingMember = projectMemberMapper.selectOne(queryWrapper);
        
        if (existingMember != null) {
            throw new IllegalArgumentException("User is already a member of this project");
        }
        
        // Create new member
        ProjectMember member = new ProjectMember();
        member.setProjectId(request.getProjectId());
        member.setUserId(request.getUserId());
        member.setRole(request.getRole() != null ? request.getRole() : "MEMBER");
        member.setJoinedAt(LocalDateTime.now());
        
        projectMemberMapper.insert(member);
        return member;
    }
    
    /**
     * T005-03: Get list of project members
     */
    public List<MemberResponse> getProjectMembers(Long projectId) {
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        List<ProjectMember> members = projectMemberMapper.selectList(queryWrapper);
        
        return members.stream().map(member -> {
            MemberResponse response = new MemberResponse();
            response.setId(member.getId());
            response.setUserId(member.getUserId());
            response.setRole(member.getRole());
            response.setJoinedAt(member.getJoinedAt());
            
            // Get user information
            User user = userMapper.selectById(member.getUserId());
            if (user != null) {
                response.setUsername(user.getUsername());
                response.setEmail(user.getEmail());
            }
            
            return response;
        }).collect(Collectors.toList());
    }
    
    /**
     * T005-04: Remove a member from a project with permission check
     */
    @Transactional
    public void removeMember(Long projectId, Long memberId, Long requestUserId) {
        // Validate project exists
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        }
        
        // Check if requestUser is the project owner (AC4: only owner can remove)
        if (!project.getOwnerId().equals(requestUserId)) {
            throw new IllegalArgumentException("Only project owner can remove members");
        }
        
        // Validate member exists
        ProjectMember member = projectMemberMapper.selectById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }
        
        // Check if member belongs to this project
        if (!member.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("Member does not belong to this project");
        }
        
        // AC4: Cannot remove yourself
        if (member.getUserId().equals(requestUserId)) {
            throw new IllegalArgumentException("Cannot remove yourself from the project");
        }
        
        projectMemberMapper.deleteById(memberId);
    }
    
    /**
     * AC5: Check if a user is a member of a project
     */
    public boolean isProjectMember(Long projectId, Long userId) {
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                   .eq("user_id", userId);
        ProjectMember member = projectMemberMapper.selectOne(queryWrapper);
        return member != null;
    }
}
