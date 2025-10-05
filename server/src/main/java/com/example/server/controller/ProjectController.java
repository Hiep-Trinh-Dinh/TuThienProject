package com.example.server.controller;

import com.example.server.dto.request.ProjectDTO;
import com.example.server.entity.Project;
import com.example.server.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectService.getActiveProjects();
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProjectDTO>> searchProjects(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projects = projectService.searchProjects(q, category, status, sortBy, pageable);
        Page<ProjectDTO> projectDTOs = projects.map(ProjectDTO::fromEntity);
        return ResponseEntity.ok(projectDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(p -> ResponseEntity.ok(ProjectDTO.fromEntity(p)))
                     .orElse(ResponseEntity.notFound().build());
    }
    
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByCategory(@PathVariable String category) {
        try {
            Project.Category categoryEnum = Project.Category.valueOf(category.toLowerCase());
            List<Project> projects = projectService.getProjectsByCategory(categoryEnum);
            List<ProjectDTO> projectDTOs = projects.stream()
                    .map(ProjectDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projectDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    @GetMapping("/sort/{sortBy}")
    public ResponseEntity<List<ProjectDTO>> getProjectsSortedBy(@PathVariable String sortBy) {
        List<Project> projects = projectService.getProjectsSortedBy(sortBy);
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }
    
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody Project project) {
        Project savedProject = projectService.saveProject(project);
        return ResponseEntity.ok(ProjectDTO.fromEntity(savedProject));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setProjectId(id);
        Project updatedProject = projectService.saveProject(project);
        return ResponseEntity.ok(ProjectDTO.fromEntity(updatedProject));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}