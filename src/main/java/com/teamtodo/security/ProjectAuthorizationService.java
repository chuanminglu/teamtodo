package com.teamtodo.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.mapper.ProjectMemberMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ProjectAuthorizationService {

    private final ProjectMemberMapper projectMemberMapper;

    public ProjectAuthorizationService(ProjectMemberMapper projectMemberMapper) {
        this.projectMemberMapper = projectMemberMapper;
    }

    public void requireProjectMember(Long projectId, Long userId) {
        if (findMembership(projectId, userId) == null) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    public void requireProjectAdmin(Long projectId, Long userId) {
        ProjectMember member = findMembership(projectId, userId);
        if (member == null) {
            throw new AccessDeniedException("Forbidden");
        }
        String role = member.getRole() == null ? "" : member.getRole().toUpperCase(Locale.ROOT);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    private ProjectMember findMembership(Long projectId, Long userId) {
        QueryWrapper<ProjectMember> query = new QueryWrapper<>();
        query.eq("project_id", projectId).eq("user_id", userId);
        return projectMemberMapper.selectOne(query);
    }
}
