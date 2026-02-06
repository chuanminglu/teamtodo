package com.teamtodo.service;

import com.teamtodo.dto.AddMemberRequest;
import com.teamtodo.dto.MemberResponse;
import com.teamtodo.entity.Project;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.entity.User;
import com.teamtodo.mapper.ProjectMapper;
import com.teamtodo.mapper.ProjectMemberMapper;
import com.teamtodo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for ProjectMemberService following TDD
 */
@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {
    
    @Mock
    private ProjectMemberMapper projectMemberMapper;
    
    @Mock
    private ProjectMapper projectMapper;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private ProjectMemberService projectMemberService;
    
    private Project testProject;
    private User testUser;
    private User testOwner;
    
    @BeforeEach
    void setUp() {
        testOwner = new User();
        testOwner.setId(1L);
        testOwner.setUsername("owner");
        testOwner.setEmail("owner@test.com");
        
        testUser = new User();
        testUser.setId(2L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setOwnerId(1L);
    }
    
    /**
     * T005-02: Test adding a member to a project with duplicate validation
     */
    @Test
    void testAddMember_Success() {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(2L);
        request.setRole("MEMBER");
        
        when(projectMapper.selectById(1L)).thenReturn(testProject);
        when(userMapper.selectById(2L)).thenReturn(testUser);
        when(projectMemberMapper.selectOne(any())).thenReturn(null); // No existing member
        when(projectMemberMapper.insert(any(ProjectMember.class))).thenReturn(1);
        
        // Act
        ProjectMember result = projectMemberService.addMember(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProjectId());
        assertEquals(2L, result.getUserId());
        assertEquals("MEMBER", result.getRole());
        verify(projectMemberMapper, times(1)).insert(any(ProjectMember.class));
    }
    
    @Test
    void testAddMember_DuplicateMember_ThrowsException() {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(2L);
        request.setRole("MEMBER");
        
        ProjectMember existingMember = new ProjectMember();
        existingMember.setProjectId(1L);
        existingMember.setUserId(2L);
        
        when(projectMapper.selectById(1L)).thenReturn(testProject);
        when(userMapper.selectById(2L)).thenReturn(testUser);
        when(projectMemberMapper.selectOne(any())).thenReturn(existingMember);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectMemberService.addMember(request);
        });
        
        assertTrue(exception.getMessage().contains("already a member"));
        verify(projectMemberMapper, never()).insert(any(ProjectMember.class));
    }
    
    @Test
    void testAddMember_ProjectNotFound_ThrowsException() {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(999L);
        request.setUserId(2L);
        
        when(projectMapper.selectById(999L)).thenReturn(null);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectMemberService.addMember(request);
        });
        
        assertTrue(exception.getMessage().contains("Project not found"));
    }
    
    @Test
    void testAddMember_UserNotFound_ThrowsException() {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(999L);
        
        when(projectMapper.selectById(1L)).thenReturn(testProject);
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectMemberService.addMember(request);
        });
        
        assertTrue(exception.getMessage().contains("User not found"));
    }
    
    /**
     * T005-03: Test listing project members
     */
    @Test
    void testGetProjectMembers_Success() {
        // Arrange
        Long projectId = 1L;
        
        ProjectMember member1 = new ProjectMember();
        member1.setId(1L);
        member1.setProjectId(projectId);
        member1.setUserId(1L);
        member1.setRole("OWNER");
        
        ProjectMember member2 = new ProjectMember();
        member2.setId(2L);
        member2.setProjectId(projectId);
        member2.setUserId(2L);
        member2.setRole("MEMBER");
        
        when(projectMemberMapper.selectList(any())).thenReturn(Arrays.asList(member1, member2));
        when(userMapper.selectById(1L)).thenReturn(testOwner);
        when(userMapper.selectById(2L)).thenReturn(testUser);
        
        // Act
        List<MemberResponse> result = projectMemberService.getProjectMembers(projectId);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("owner", result.get(0).getUsername());
        assertEquals("testuser", result.get(1).getUsername());
    }
    
    /**
     * T005-04: Test removing a member from a project with permission check
     */
    @Test
    void testRemoveMember_Success() {
        // Arrange
        Long projectId = 1L;
        Long memberId = 2L;
        Long requestUserId = 1L; // Owner
        
        ProjectMember member = new ProjectMember();
        member.setId(memberId);
        member.setProjectId(projectId);
        member.setUserId(3L); // Different from owner
        
        when(projectMapper.selectById(projectId)).thenReturn(testProject);
        when(projectMemberMapper.selectById(memberId)).thenReturn(member);
        when(projectMemberMapper.deleteById(memberId)).thenReturn(1);
        
        // Act
        projectMemberService.removeMember(projectId, memberId, requestUserId);
        
        // Assert
        verify(projectMemberMapper, times(1)).deleteById(memberId);
    }
    
    @Test
    void testRemoveMember_NotOwner_ThrowsException() {
        // Arrange
        Long projectId = 1L;
        Long memberId = 2L;
        Long requestUserId = 3L; // Not owner
        
        when(projectMapper.selectById(projectId)).thenReturn(testProject);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectMemberService.removeMember(projectId, memberId, requestUserId);
        });
        
        assertTrue(exception.getMessage().contains("Only project owner can remove members"));
    }
    
    @Test
    void testRemoveMember_RemovingSelf_ThrowsException() {
        // Arrange
        Long projectId = 1L;
        Long memberId = 1L;
        Long requestUserId = 1L; // Owner trying to remove themselves
        
        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setId(memberId);
        ownerMember.setProjectId(projectId);
        ownerMember.setUserId(requestUserId);
        
        when(projectMapper.selectById(projectId)).thenReturn(testProject);
        when(projectMemberMapper.selectById(memberId)).thenReturn(ownerMember);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectMemberService.removeMember(projectId, memberId, requestUserId);
        });
        
        assertTrue(exception.getMessage().contains("Cannot remove yourself"));
    }
    
    @Test
    void testRemoveMember_MemberNotFound_ThrowsException() {
        // Arrange
        Long projectId = 1L;
        Long memberId = 999L;
        Long requestUserId = 1L;
        
        when(projectMapper.selectById(projectId)).thenReturn(testProject);
        when(projectMemberMapper.selectById(memberId)).thenReturn(null);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectMemberService.removeMember(projectId, memberId, requestUserId);
        });
        
        assertTrue(exception.getMessage().contains("Member not found"));
    }
    
    /**
     * Test checking if user is a member of project (AC5)
     */
    @Test
    void testIsProjectMember_Success() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;
        
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        
        when(projectMemberMapper.selectOne(any())).thenReturn(member);
        
        // Act
        boolean result = projectMemberService.isProjectMember(projectId, userId);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void testIsProjectMember_NotMember() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;
        
        when(projectMemberMapper.selectOne(any())).thenReturn(null);
        
        // Act
        boolean result = projectMemberService.isProjectMember(projectId, userId);
        
        // Assert
        assertFalse(result);
    }
}
