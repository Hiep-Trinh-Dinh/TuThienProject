package com.example.server.mapper;


import com.example.server.dto.request.PermissionRequest;
import com.example.server.dto.response.PermissionResponse;
import com.example.server.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
