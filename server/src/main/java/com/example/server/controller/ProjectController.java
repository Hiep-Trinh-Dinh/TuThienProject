package com.example.server.controller;

import com.example.server.dto.request.ProjectDTO;
import com.example.server.dto.response.ProjectStatsResponse;
import com.example.server.entity.Project;
import com.example.server.service.FileStorageService;
import com.example.server.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private com.example.server.service.DonationService donationService;

    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectService.getActiveProjects();
        List<ProjectDTO> projectDTOs = projects.stream()
            .map(this::mapProjectWithStats)
            .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/full")
    public ResponseEntity<List<ProjectDTO>> getAllByOrderByStatusAsc() {
        List<Project> projects = projectService.getAllByOrderByStatusAsc();
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(this::mapProjectWithStats)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/donated")
    public ResponseEntity<Page<ProjectDTO>> getDonatedProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "0") Long userId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projects = projectService.getDonatedProjects(pageable, userId);
        Page<ProjectDTO> projectDTOs = projects.map(this::mapProjectWithStats);
        return ResponseEntity.ok(projectDTOs);
    }



    @GetMapping("/search")
    public ResponseEntity<Page<ProjectDTO>> searchProjects(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false, defaultValue = "0") Long userId) {

        if ((userId == null || userId <= 0) && (status == null || status.isEmpty())) {
            status = "active";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projects = projectService.searchProjects(q, category, status, sortBy, pageable, userId);
        Page<ProjectDTO> projectDTOs = projects.map(this::mapProjectWithStats);
        return ResponseEntity.ok(projectDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(p -> {
            ProjectDTO dto = mapProjectWithStats(p);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{userId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByUserId(@PathVariable Long userId) {
        List<Project> projects = projectService.getProjectsByUserId(userId);
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(this::mapProjectWithStats)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<Long> getTotalProjectsByUserId(@PathVariable Long userId) {
        Long count = projectService.countProjectsByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/projectStats/{userId}")
    public ResponseEntity<ProjectStatsResponse> getProjectStats(@PathVariable Long userId) {
        ProjectStatsResponse projectStats = projectService.getProjectStats(userId);
        return ResponseEntity.ok(projectStats);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByCategory(@PathVariable String category) {
        try {
            Project.Category categoryEnum = Project.Category.valueOf(category.toLowerCase());
            List<Project> projects = projectService.getProjectsByCategory(categoryEnum);
            List<ProjectDTO> projectDTOs = projects.stream()
                    .map(this::mapProjectWithStats)
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
                .map(this::mapProjectWithStats)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }
    
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody Project project) {
        Project savedProject = projectService.saveProject(project);
        return ResponseEntity.ok(mapProjectWithStats(savedProject));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setProjectId(id);
        Project updatedProject = projectService.saveProject(project);
        return ResponseEntity.ok(mapProjectWithStats(updatedProject));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadProjectImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileStorageService.storeProjectImage(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Không thể upload ảnh: " + e.getMessage()));
        }
    }

    private ProjectDTO mapProjectWithStats(Project project) {
        ProjectDTO dto = ProjectDTO.fromEntity(project);
        BigDecimal raisedAmount = donationService.getTotalDonationsByProject(project.getProjectId());
        dto.setRaisedAmount(raisedAmount);
        dto.setProgressPercentage(calculateProgressPercentage(raisedAmount, project.getGoalAmount()));
        dto.setDonorCount(donationService.countDonorsByProject(project.getProjectId()));
        return dto;
    }

    private double calculateProgressPercentage(BigDecimal raisedAmount, BigDecimal goalAmount) {
        if (goalAmount == null || goalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        BigDecimal safeRaised = raisedAmount != null ? raisedAmount : BigDecimal.ZERO;
        BigDecimal percentage = safeRaised
                .divide(goalAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return Math.min(100.0, percentage.doubleValue());
    }
}