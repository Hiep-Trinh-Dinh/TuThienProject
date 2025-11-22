package com.example.server.service;

import com.example.server.entity.Project;
import com.example.server.repository.ProjectRepository;
import com.example.server.dto.response.ProjectStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProjects_ReturnsList() {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        given(projectRepository.findAll()).willReturn(projects);
        List<Project> result = projectService.getAllProjects();
        assertThat(result).hasSize(2);
    }

    @Test
    void getActiveProjects_ReturnsActiveList() {
        List<Project> actives = Arrays.asList(new Project(), new Project());
        given(projectRepository.findByStatus(Project.Status.active)).willReturn(actives);
        List<Project> result = projectService.getActiveProjects();
        assertThat(result).hasSize(2);
    }

    @Test
    void getProjectsByCategory_ReturnsList() {
        Project.Category category = Project.Category.y_te;
        given(projectRepository.findByCategory(category)).willReturn(Arrays.asList(new Project()));
        List<Project> result = projectService.getProjectsByCategory(category);
        assertThat(result).hasSize(1);
    }

    @Test
    void searchProjects_ReturnsList() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, null, null, null, PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getProjectsSortedBy_ReturnsSortedList() {
        List<Project> projects = Arrays.asList(new Project());
        given(projectRepository.findActiveProjectsOrderByCreatedAt()).willReturn(projects);
        List<Project> result = projectService.getProjectsSortedBy("newest");
        assertThat(result).hasSize(1);

        given(projectRepository.findActiveProjectsOrderByRaisedAmount()).willReturn(projects);
        result = projectService.getProjectsSortedBy("progress");
        assertThat(result).hasSize(1);

        given(projectRepository.findActiveProjectsOrderByEndDate()).willReturn(projects);
        result = projectService.getProjectsSortedBy("ending-soon");
        assertThat(result).hasSize(1);
    }

    @Test
    void getProjectById_Found() {
        Project project = new Project();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));
        Optional<Project> result = projectService.getProjectById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void getProjectById_NotFound() {
        given(projectRepository.findById(anyLong())).willReturn(Optional.empty());
        Optional<Project> result = projectService.getProjectById(21L);
        assertThat(result).isNotPresent();
    }

    @Test
    void saveProject_ReturnsSavedProject() {
        Project project = new Project();
        given(projectRepository.save(project)).willReturn(project);
        Project saved = projectService.saveProject(project);
        assertThat(saved).isNotNull();
    }

    @Test
    void getProjectsByUserId_ReturnsList() {
        List<Project> projects = Arrays.asList(new Project());
        given(projectRepository.findByOrgId(1L)).willReturn(projects);
        List<Project> result = projectService.getProjectsByUserId(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    void countProjectsByUser_ReturnsCount() {
        given(projectRepository.countUniqueProjectsByUser(2L)).willReturn(3L);
        Long count = projectService.countProjectsByUser(2L);
        assertThat(count).isEqualTo(3L);
    }
    
    @Test
    void getProjectStats_ReturnsStats() {
        ProjectStatsResponse stats = new ProjectStatsResponse(1L,2L,3L,null);
        given(projectRepository.getProjectStats(1L)).willReturn(stats);
        ProjectStatsResponse result = projectService.getProjectStats(1L);
        assertThat(result.totalProjects()).isEqualTo(1L);
    }

    @Test
    void searchProjects_WithUserId_CallsPersonalProjects() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findPersonalProjectsWithFilters(any(), any(), any(), any(), anyLong(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, null, null, null, PageRequest.of(0, 5), 1L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchProjects_WithCategory_CallsWithCategory() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, "y_te", null, null, PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchProjects_WithStatus_CallsWithStatus() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, null, "active", null, PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchProjects_WithSortByNewest_CallsWithSort() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, null, null, "newest", PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchProjects_WithSortByProgress_CallsWithSort() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, null, null, "progress", PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchProjects_WithSortByEndingSoon_CallsWithSort() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, null, null, "ending-soon", PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getDonatedProjects_ReturnsPage() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findPersonalProjects(anyLong(), any(Pageable.class))).willReturn(page);
        Page<Project> result = projectService.getDonatedProjects(PageRequest.of(0, 5), 1L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchProjects_InvalidCategory_IgnoresCategory() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects(null, "invalid", null, null, PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void updateProjectRaisedAmount_ProjectFound_ReturnsProject() {
        Project project = new Project();
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));
        given(projectRepository.save(project)).willReturn(project);
        Project result = projectService.updateProjectRaisedAmount(1L);
        assertThat(result).isNotNull();
    }

    @Test
    void updateProjectRaisedAmount_ProjectNotFound_ReturnsNull() {
        given(projectRepository.findById(1L)).willReturn(Optional.empty());
        Project result = projectService.updateProjectRaisedAmount(1L);
        assertThat(result).isNull();
    }

    @Test
    void deleteProject_CallsRepositoryDelete() {
        doNothing().when(projectRepository).deleteById(1L);
        projectService.deleteProject(1L);
        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void getProjectsSortedBy_Default_ReturnsNewest() {
        List<Project> projects = Arrays.asList(new Project());
        given(projectRepository.findActiveProjectsOrderByCreatedAt()).willReturn(projects);
        List<Project> result = projectService.getProjectsSortedBy("unknown");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchProjects_WithKeyword_CallsWithKeyword() {
        Page<Project> page = new PageImpl<>(Arrays.asList(new Project()));
        given(projectRepository.findProjectsWithFilters(any(), any(), any(), any(), any(Pageable.class)))
            .willReturn(page);
        Page<Project> result = projectService.searchProjects("test", null, null, null, PageRequest.of(0, 5), 0L);
        assertThat(result.getContent()).hasSize(1);
    }
}
