package com.example.server.mapper;

import com.example.server.dto.response.UserResponse;
import com.example.server.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
