package com.teamtodo.controller;

import com.teamtodo.dto.AddMemberRequest;
import com.teamtodo.dto.MemberResponse;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Project Member Management
 * Implements APIs for T005-02, T005-03, T005-04
 */
@RestController
@RequestMapping("/projects")
@Validated
public class ProjectMemberController {
    
    @Autowired
    private ProjectMemberService projectMemberService;
    
    /**
     * T005-02: Add a member to a project
     * AC1: Project admin can search and invite users
     * 
     * @param request AddMemberRequest containing projectId, userId, and role
     * @return ProjectMember entity
     */
    @PostMapping("/members")
    public ResponseEntity<?> addMember(@Valid @RequestBody AddMemberRequest request) {
        try {
            ProjectMember member = projectMemberService.addMember(request);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * T005-03: Get list of project members
     * AC2: Invited users can see new projects
     * AC3: Member list shows username, role, join time
     * 
     * @param projectId Project ID
     * @return List of MemberResponse
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        List<MemberResponse> members = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }
    
    /**
     * T005-04: Remove a member from a project
     * AC4: Project Owner can remove members (cannot remove self)
     * 
     * @param projectId Project ID
     * @param memberId Member ID to remove
     * @param requestUserId ID of user making the request (should be project owner)
     * @return Success message
     */
    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam Long requestUserId) {
        try {
            projectMemberService.removeMember(projectId, memberId, requestUserId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Member removed successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * AC5: Check if user is a member of project (for access control)
     * 
     * @param projectId Project ID
     * @param userId User ID
     * @return boolean indicating membership
     */
    @GetMapping("/{projectId}/members/check")
    public ResponseEntity<Map<String, Boolean>> checkMembership(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        boolean isMember = projectMemberService.isProjectMember(projectId, userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isMember", isMember);
        return ResponseEntity.ok(response);
    }
}
