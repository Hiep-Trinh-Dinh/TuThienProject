package com.example.server.service;

import com.example.server.dto.request.PermissionRequest;
import com.example.server.dto.response.PermissionResponse;
import com.example.server.entity.Permission;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.mapper.PermissionMapper;
import com.example.server.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('admin')")
    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @PreAuthorize("hasRole('admin')")
    public PermissionResponse updateDescription(PermissionRequest request){
        var existing = permissionRepository.findByName(request.getName()).orElseThrow(
                ()->new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        existing.setDescription(request.getDescription());
        permissionRepository.save(existing);
        return permissionMapper.toPermissionResponse(existing);
    }

    @PreAuthorize("hasRole('admin')")
    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @PreAuthorize("hasRole('admin')")
    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
