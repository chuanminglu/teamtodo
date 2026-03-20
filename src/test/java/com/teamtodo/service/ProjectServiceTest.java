package com.teamtodo.service;

import com.teamtodo.dto.CreateProjectRequest;
import com.teamtodo.dto.ProjectResponse;
import com.teamtodo.entity.Project;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectService following TDD
 * Covers T004-01 to T004-04 and acceptance criteria AC1, AC3, AC4, AC5
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectMemberMapper projectMemberMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ProjectService projectService;

    private User testOwner;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testOwner = new User();
        testOwner.setId(1L);
        testOwner.setUsername("admin");
        testOwner.setEmail("admin@test.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setOwnerId(1L);
        testProject.setCreatedAt(LocalDateTime.now());
        testProject.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * T004-02 / AC1: Test creating a project successfully
     */
    @Test
    void testCreateProject_Success() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        request.setDescription("A great project");
        request.setOwnerId(1L);

        when(userMapper.selectById(1L)).thenReturn(testOwner);
        when(projectMapper.insert(any(Project.class))).thenReturn(1);
        when(projectMemberMapper.insert(any())).thenReturn(1);

        // Act
        ProjectResponse response = projectService.createProject(request);

        // Assert
        assertNotNull(response);
        assertEquals("My Project", response.getName());
        assertEquals("A great project", response.getDescription());
        assertEquals(1L, response.getOwnerId());
        verify(projectMapper, times(1)).insert(any(Project.class));
    }

    /**
     * T004-02 / AC3: Test that creator is automatically added as OWNER member
     */
    @Test
    void testCreateProject_CreatorBecomesOwnerMember() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        request.setOwnerId(1L);

        when(userMapper.selectById(1L)).thenReturn(testOwner);
        when(projectMapper.insert(any(Project.class))).thenReturn(1);
        when(projectMemberMapper.insert(any())).thenReturn(1);

        // Act
        projectService.createProject(request);

        // Assert: owner member should be inserted with OWNER role
        verify(projectMemberMapper, times(1)).insert(argThat(member ->
                "OWNER".equals(member.getRole()) && Long.valueOf(1L).equals(member.getUserId())
        ));
    }

    /**
     * T004-02 / AC4: Test that project name is required (owner not found)
     */
    @Test
    void testCreateProject_OwnerNotFound_ThrowsException() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        request.setOwnerId(999L);

        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                projectService.createProject(request)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(projectMapper, never()).insert(any());
    }

    /**
     * T004-02 / AC4: Test that description is optional
     */
    @Test
    void testCreateProject_DescriptionOptional_Success() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("My Project");
        request.setDescription(null); // no description
        request.setOwnerId(1L);

        when(userMapper.selectById(1L)).thenReturn(testOwner);
        when(projectMapper.insert(any(Project.class))).thenReturn(1);
        when(projectMemberMapper.insert(any())).thenReturn(1);

        // Act
        ProjectResponse response = projectService.createProject(request);

        // Assert
        assertNotNull(response);
        assertNull(response.getDescription());
    }

    /**
     * T004-03 / AC5: Test listing projects sorted by createdAt descending
     */
    @Test
    void testListProjects_SortedByCreatedAtDesc() {
        // Arrange
        Project older = new Project();
        older.setId(1L);
        older.setName("Older Project");
        older.setOwnerId(1L);
        older.setCreatedAt(LocalDateTime.now().minusDays(1));

        Project newer = new Project();
        newer.setId(2L);
        newer.setName("Newer Project");
        newer.setOwnerId(1L);
        newer.setCreatedAt(LocalDateTime.now());

        // The mapper already returns in sorted order (via QueryWrapper orderByDesc)
        when(projectMapper.selectList(any())).thenReturn(Arrays.asList(newer, older));

        // Act
        List<ProjectResponse> result = projectService.listProjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Newer Project", result.get(0).getName());
        assertEquals("Older Project", result.get(1).getName());
    }

    /**
     * T004-03: Test listing projects returns empty list when no projects
     */
    @Test
    void testListProjects_EmptyList() {
        // Arrange
        when(projectMapper.selectList(any())).thenReturn(List.of());

        // Act
        List<ProjectResponse> result = projectService.listProjects();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * T004-04: Test getting project detail by ID
     */
    @Test
    void testGetProject_Success() {
        // Arrange
        when(projectMapper.selectById(1L)).thenReturn(testProject);

        // Act
        ProjectResponse response = projectService.getProject(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Project", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(1L, response.getOwnerId());
    }

    /**
     * T004-04: Test getting project detail with non-existent ID
     */
    @Test
    void testGetProject_NotFound_ThrowsException() {
        // Arrange
        when(projectMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                projectService.getProject(999L)
        );

        assertTrue(exception.getMessage().contains("Project not found"));
    }
}
