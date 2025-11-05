package com.example.server.mapper;

import com.example.server.dto.response.UserResponse;
import com.example.server.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "authProvider", expression = "java(user.getAuthProvider() != null ? user.getAuthProvider().toString() : null)")
    UserResponse toUserResponse(User user);
}
