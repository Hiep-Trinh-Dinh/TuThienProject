package com.example.server.mapper;

import com.example.server.dto.request.RoleRequest;
import com.example.server.dto.response.RoleResponse;
import com.example.server.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {
    @Mapping(target = "permissions",ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
