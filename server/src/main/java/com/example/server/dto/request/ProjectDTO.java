package com.example.server.dto.request;

import com.example.server.entity.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long projectId;
    private Long orgId;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private BigDecimal goalAmount;
    private BigDecimal raisedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
    
    // Computed fields for frontend
    private Integer daysLeft;
    private Double progressPercentage;
    private Long donorCount; 
    
    public static ProjectDTO fromEntity(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectId(project.getProjectId());
        dto.setOrgId(project.getOrgId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setImageUrl(project.getImageUrl());
        dto.setCategory(project.getCategory().name().toLowerCase());
        dto.setGoalAmount(project.getGoalAmount());
        dto.setRaisedAmount(project.getRaisedAmount());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setStatus(project.getStatus().name().toLowerCase());
        dto.setCreatedAt(project.getCreatedAt());
        
        // Calculate days left
        if (project.getEndDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), project.getEndDate());
            dto.setDaysLeft(Math.max(0, (int) days));
        }
        
        // Calculate progress percentage
        if (project.getGoalAmount() != null && project.getRaisedAmount() != null && 
            project.getGoalAmount().compareTo(BigDecimal.ZERO) > 0) {
            double progress = project.getRaisedAmount()
                                   .divide(project.getGoalAmount(), 4, RoundingMode.HALF_UP)
                                   .multiply(BigDecimal.valueOf(100)).doubleValue();
            dto.setProgressPercentage(Math.min(100.0, progress));
        } else {
            dto.setProgressPercentage(0.0);
        }
        
        return dto;
    }
}
