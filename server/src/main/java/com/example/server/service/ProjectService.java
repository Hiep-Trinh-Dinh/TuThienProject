package com.example.server.service;

import com.example.server.entity.Project;
import com.example.server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public List<Project> getActiveProjects() {
        return projectRepository.findByStatus(Project.Status.active);
    }
    
    
    public List<Project> getProjectsByCategory(Project.Category category) {
        return projectRepository.findByCategory(category);
    }
    
    public List<Project> searchProjects(String keyword) {
        return projectRepository.findByKeyword(keyword);
    }

    public Page<Project> searchProjects(String q, String category, String status, String sortBy, Pageable pageable) {
        // Create pageable with sorting
        Pageable sortedPageable = pageable;
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "newest":
                    sortedPageable = PageRequest.of(
                        pageable.getPageNumber(), 
                        pageable.getPageSize(), 
                        Sort.by("createdAt").descending()
                    );
                    break;
                case "progress":
                    sortedPageable = PageRequest.of(
                        pageable.getPageNumber(), 
                        pageable.getPageSize(), 
                        Sort.by("raisedAmount").descending()
                    );
                    break;
                case "ending-soon":
                    sortedPageable = PageRequest.of(
                        pageable.getPageNumber(), 
                        pageable.getPageSize(), 
                        Sort.by("endDate").ascending()
                    );
                    break;
                default:
                    sortedPageable = PageRequest.of(
                        pageable.getPageNumber(), 
                        pageable.getPageSize(), 
                        Sort.by("createdAt").descending()
                    );
                    break;
            }
        }
        
        // Convert string to enum
        Project.Category categoryEnum = null;
        if (category != null && !category.isEmpty()) {
            try {
                categoryEnum = Project.Category.valueOf(category.toLowerCase());
            } catch (IllegalArgumentException e) {
                // Invalid category, will be ignored
            }
        }
        
        Project.Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Project.Status.valueOf(status.toLowerCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, will be ignored
            }
        }
        
        return projectRepository.findProjectsWithFilters(q, categoryEnum, statusEnum, sortBy, sortedPageable);
    }
    
    public List<Project> getProjectsSortedBy(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "newest":
                return projectRepository.findActiveProjectsOrderByCreatedAt();
            case "progress":
                return projectRepository.findActiveProjectsOrderByRaisedAmount();
            case "ending-soon":
                return projectRepository.findActiveProjectsOrderByEndDate();
            default:
                return projectRepository.findActiveProjectsOrderByCreatedAt();
        }
    }
    
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }
    
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
    
    public Project updateProjectRaisedAmount(Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            // TODO: Implement donation calculation when donation functionality is added
            return projectRepository.save(project);
        }
        return null;
    }
}
