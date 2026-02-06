package com.teamtodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.AddMemberRequest;
import com.teamtodo.dto.MemberResponse;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.service.ProjectMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for ProjectMemberController
 */
@WebMvcTest(ProjectMemberController.class)
class ProjectMemberControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProjectMemberService projectMemberService;
    
    /**
     * T005-02: Test add member API endpoint
     */
    @Test
    void testAddMember_Success() throws Exception {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(2L);
        request.setRole("MEMBER");
        
        ProjectMember member = new ProjectMember();
        member.setId(1L);
        member.setProjectId(1L);
        member.setUserId(2L);
        member.setRole("MEMBER");
        member.setJoinedAt(LocalDateTime.now());
        
        when(projectMemberService.addMember(any(AddMemberRequest.class))).thenReturn(member);
        
        // Act & Assert
        mockMvc.perform(post("/projects/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.role").value("MEMBER"));
        
        verify(projectMemberService, times(1)).addMember(any(AddMemberRequest.class));
    }
    
    @Test
    void testAddMember_ValidationError() throws Exception {
        // Arrange - missing required fields
        AddMemberRequest request = new AddMemberRequest();
        // projectId and userId are null
        
        // Act & Assert
        mockMvc.perform(post("/projects/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(projectMemberService, never()).addMember(any(AddMemberRequest.class));
    }
    
    @Test
    void testAddMember_DuplicateMember() throws Exception {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(2L);
        request.setRole("MEMBER");
        
        when(projectMemberService.addMember(any(AddMemberRequest.class)))
                .thenThrow(new IllegalArgumentException("User is already a member of this project"));
        
        // Act & Assert
        mockMvc.perform(post("/projects/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User is already a member of this project"));
    }
    
    /**
     * T005-03: Test get project members API endpoint
     */
    @Test
    void testGetProjectMembers_Success() throws Exception {
        // Arrange
        Long projectId = 1L;
        
        MemberResponse member1 = new MemberResponse();
        member1.setId(1L);
        member1.setUserId(1L);
        member1.setUsername("owner");
        member1.setEmail("owner@test.com");
        member1.setRole("OWNER");
        member1.setJoinedAt(LocalDateTime.now());
        
        MemberResponse member2 = new MemberResponse();
        member2.setId(2L);
        member2.setUserId(2L);
        member2.setUsername("member");
        member2.setEmail("member@test.com");
        member2.setRole("MEMBER");
        member2.setJoinedAt(LocalDateTime.now());
        
        List<MemberResponse> members = Arrays.asList(member1, member2);
        
        when(projectMemberService.getProjectMembers(projectId)).thenReturn(members);
        
        // Act & Assert
        mockMvc.perform(get("/projects/{projectId}/members", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("owner"))
                .andExpect(jsonPath("$[1].username").value("member"));
        
        verify(projectMemberService, times(1)).getProjectMembers(projectId);
    }
    
    /**
     * T005-04: Test remove member API endpoint
     */
    @Test
    void testRemoveMember_Success() throws Exception {
        // Arrange
        Long projectId = 1L;
        Long memberId = 2L;
        Long requestUserId = 1L;
        
        doNothing().when(projectMemberService).removeMember(projectId, memberId, requestUserId);
        
        // Act & Assert
        mockMvc.perform(delete("/projects/{projectId}/members/{memberId}", projectId, memberId)
                .param("requestUserId", requestUserId.toString()))
                .andExpect(status().isOk());
        
        verify(projectMemberService, times(1)).removeMember(projectId, memberId, requestUserId);
    }
    
    @Test
    void testRemoveMember_NotOwner() throws Exception {
        // Arrange
        Long projectId = 1L;
        Long memberId = 2L;
        Long requestUserId = 3L; // Not owner
        
        doThrow(new IllegalArgumentException("Only project owner can remove members"))
                .when(projectMemberService).removeMember(projectId, memberId, requestUserId);
        
        // Act & Assert
        mockMvc.perform(delete("/projects/{projectId}/members/{memberId}", projectId, memberId)
                .param("requestUserId", requestUserId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only project owner can remove members"));
    }
    
    @Test
    void testRemoveMember_CannotRemoveSelf() throws Exception {
        // Arrange
        Long projectId = 1L;
        Long memberId = 1L;
        Long requestUserId = 1L; // Same as member
        
        doThrow(new IllegalArgumentException("Cannot remove yourself from the project"))
                .when(projectMemberService).removeMember(projectId, memberId, requestUserId);
        
        // Act & Assert
        mockMvc.perform(delete("/projects/{projectId}/members/{memberId}", projectId, memberId)
                .param("requestUserId", requestUserId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot remove yourself from the project"));
    }
}
