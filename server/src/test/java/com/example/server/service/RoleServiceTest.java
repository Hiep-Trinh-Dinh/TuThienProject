package com.example.server.service;

import com.example.server.dto.request.RoleRequest;
import com.example.server.dto.response.RoleResponse;
import com.example.server.entity.Role;
import com.example.server.mapper.RoleMapper;
import com.example.server.repository.PermissionRepository;
import com.example.server.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceTest {
    @Mock private RoleRepository roleRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private RoleMapper roleMapper;
    @InjectMocks private RoleService roleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ReturnsRoleResponse() {
        RoleRequest request = new RoleRequest();
        request.setPermissions(new HashSet<>(Arrays.asList("p1", "p2")));
        Role roleEntity = new Role();
        when(roleMapper.toRole(request)).thenReturn(roleEntity);
        when(permissionRepository.findAllById(request.getPermissions())).thenReturn(Collections.emptyList());
        when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
        RoleResponse resp = new RoleResponse();
        when(roleMapper.toRoleResponse(roleEntity)).thenReturn(resp);
        RoleResponse result = roleService.create(request);
        assertThat(result).isNotNull();
    }

    @Test
    void getAll_ReturnsList() {
        Role role = new Role();
        RoleResponse resp = new RoleResponse();
        when(roleRepository.findAll()).thenReturn(Collections.singletonList(role));
        when(roleMapper.toRoleResponse(role)).thenReturn(resp);
        List<RoleResponse> result = roleService.getAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void delete_CallsRepoDeleteById() {
        String roleId = "admin";
        doNothing().when(roleRepository).deleteById(roleId);
        roleService.delete(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void create_WithPermissions_SetsPermissions() {
        RoleRequest request = new RoleRequest();
        request.setPermissions(new HashSet<>(Arrays.asList("p1", "p2")));
        com.example.server.entity.Permission perm1 = new com.example.server.entity.Permission();
        perm1.setName("p1");
        com.example.server.entity.Permission perm2 = new com.example.server.entity.Permission();
        perm2.setName("p2");
        Role roleEntity = new Role();
        when(roleMapper.toRole(request)).thenReturn(roleEntity);
        when(permissionRepository.findAllById(request.getPermissions())).thenReturn(Arrays.asList(perm1, perm2));
        when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
        RoleResponse resp = new RoleResponse();
        when(roleMapper.toRoleResponse(roleEntity)).thenReturn(resp);
        RoleResponse result = roleService.create(request);
        assertThat(result).isNotNull();
        assertThat(roleEntity.getPermissions()).hasSize(2);
    }

    @Test
    void getAll_EmptyList_ReturnsEmptyList() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());
        List<RoleResponse> result = roleService.getAll();
        assertThat(result).isEmpty();
    }

    @Test
    void getAll_MultipleRoles_ReturnsList() {
        Role r1 = new Role();
        Role r2 = new Role();
        RoleResponse resp1 = new RoleResponse();
        RoleResponse resp2 = new RoleResponse();
        when(roleRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
        when(roleMapper.toRoleResponse(r1)).thenReturn(resp1);
        when(roleMapper.toRoleResponse(r2)).thenReturn(resp2);
        List<RoleResponse> result = roleService.getAll();
        assertThat(result).hasSize(2);
    }
}
