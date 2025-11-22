package com.example.server.service;

import com.example.server.dto.request.PermissionRequest;
import com.example.server.dto.response.PermissionResponse;
import com.example.server.entity.Permission;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.mapper.PermissionMapper;
import com.example.server.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionServiceTest {
    @Mock private PermissionRepository permissionRepository;
    @Mock private PermissionMapper permissionMapper;
    @InjectMocks private PermissionService permissionService;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ReturnsPermissionResponse() {
        PermissionRequest req = new PermissionRequest();
        Permission entity = new Permission();
        PermissionResponse resp = new PermissionResponse();
        when(permissionMapper.toPermission(req)).thenReturn(entity);
        when(permissionRepository.save(entity)).thenReturn(entity);
        when(permissionMapper.toPermissionResponse(entity)).thenReturn(resp);
        PermissionResponse result = permissionService.create(req);
        assertThat(result).isNotNull();
    }

    @Test
    void updateDescription_UpdateDescriptionWhenFound() {
        PermissionRequest req = new PermissionRequest();
        req.setName("abc");
        req.setDescription("desc");
        Permission entity = new Permission();
        entity.setName("abc");
        when(permissionRepository.findByName("abc")).thenReturn(Optional.of(entity));
        when(permissionRepository.save(entity)).thenReturn(entity);
        when(permissionMapper.toPermissionResponse(entity)).thenReturn(new PermissionResponse());
        PermissionResponse resp = permissionService.updateDescription(req);
        assertThat(resp).isNotNull();
        assertThat(entity.getDescription()).isEqualTo("desc");
    }

    @Test
    void updateDescription_ThrowsIfNotFound() {
        PermissionRequest req = new PermissionRequest();
        req.setName("xxx");
        when(permissionRepository.findByName("xxx")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> permissionService.updateDescription(req))
            .isInstanceOf(AppException.class)
            .extracting("errorCode").isEqualTo(ErrorCode.PERMISSION_NOT_FOUND);
    }

    @Test
    void getAll_ReturnsList() {
        Permission entity = new Permission();
        PermissionResponse resp = new PermissionResponse();
        when(permissionRepository.findAll()).thenReturn(Collections.singletonList(entity));
        when(permissionMapper.toPermissionResponse(entity)).thenReturn(resp);
        List<PermissionResponse> list = permissionService.getAll();
        assertThat(list).hasSize(1);
    }

    @Test
    void delete_CallsDeleteById() {
        doNothing().when(permissionRepository).deleteById("abc");
        permissionService.delete("abc");
        verify(permissionRepository, times(1)).deleteById("abc");
    }

    @Test
    void create_SavesPermission() {
        PermissionRequest req = new PermissionRequest();
        req.setName("READ");
        req.setDescription("Read permission");
        Permission entity = new Permission();
        PermissionResponse resp = new PermissionResponse();
        when(permissionMapper.toPermission(req)).thenReturn(entity);
        when(permissionRepository.save(entity)).thenReturn(entity);
        when(permissionMapper.toPermissionResponse(entity)).thenReturn(resp);
        PermissionResponse result = permissionService.create(req);
        assertThat(result).isNotNull();
        verify(permissionRepository).save(entity);
    }

    @Test
    void getAll_EmptyList_ReturnsEmptyList() {
        when(permissionRepository.findAll()).thenReturn(Collections.emptyList());
        List<PermissionResponse> result = permissionService.getAll();
        assertThat(result).isEmpty();
    }

    @Test
    void getAll_MultiplePermissions_ReturnsList() {
        Permission p1 = new Permission();
        Permission p2 = new Permission();
        PermissionResponse r1 = new PermissionResponse();
        PermissionResponse r2 = new PermissionResponse();
        when(permissionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        when(permissionMapper.toPermissionResponse(p1)).thenReturn(r1);
        when(permissionMapper.toPermissionResponse(p2)).thenReturn(r2);
        List<PermissionResponse> result = permissionService.getAll();
        assertThat(result).hasSize(2);
    }
}
