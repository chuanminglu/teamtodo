package com.teamtodo.controller;

import com.teamtodo.dto.CreateProjectRequest;
import com.teamtodo.dto.ProjectResponse;
import com.teamtodo.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Project Management
 * Implements APIs for T004-02, T004-03, T004-04
 */
@RestController
@RequestMapping("/projects")
@Validated
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * T004-02: Create a new project
     * AC1: Admin fills project name and description
     * AC2: Returns created project for redirect to detail page
     * AC3: Creator automatically becomes project Owner
     * AC4: Project name is required
     *
     * @param request CreateProjectRequest
     * @return Created ProjectResponse
     */
    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectRequest request) {
        try {
            ProjectResponse project = projectService.createProject(request);
            return ResponseEntity.ok(project);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * T004-03: Get list of all projects
     * AC5: Sorted by createdAt descending
     *
     * @return List of ProjectResponse
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects() {
        List<ProjectResponse> projects = projectService.listProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * T004-04: Get project detail by ID
     *
     * @param id Project ID
     * @return ProjectResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id) {
        try {
            ProjectResponse project = projectService.getProject(id);
            return ResponseEntity.ok(project);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
