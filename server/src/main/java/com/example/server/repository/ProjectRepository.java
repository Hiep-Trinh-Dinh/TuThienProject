package com.example.server.repository;

import com.example.server.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByStatus(Project.Status status);
    
    List<Project> findByCategory(Project.Category category);
    
    @Query("SELECT p FROM Project p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Project> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'active' ORDER BY p.createdAt DESC")
    List<Project> findActiveProjectsOrderByCreatedAt();
    
    @Query("SELECT p FROM Project p WHERE p.status = 'active' ORDER BY p.raisedAmount DESC")
    List<Project> findActiveProjectsOrderByRaisedAmount();
    
    @Query("SELECT p FROM Project p WHERE p.status = 'active' AND p.endDate >= CURRENT_DATE ORDER BY p.endDate ASC")
    List<Project> findActiveProjectsOrderByEndDate();

    @Query("SELECT p FROM Project p WHERE " +
            "(:q IS NULL OR :q = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
            "(:categoryEnum IS NULL OR p.category = :categoryEnum) AND " +
            "(:statusEnum IS NULL OR p.status = :statusEnum)")
    Page<Project> findProjectsWithFilters(@Param("q") String q, 
                                         @Param("categoryEnum") Project.Category categoryEnum,
                                         @Param("statusEnum") Project.Status statusEnum,
                                         @Param("sortBy") String sortBy, 
                                         Pageable pageable);
}
