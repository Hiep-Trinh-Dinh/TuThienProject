package com.example.server.service;

import com.example.server.entity.Permission;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setRoles(new HashSet<>());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername("test@example.com");
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@example.com");
        assertThat(result.getPassword()).isEqualTo("hashedPassword");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("notfound@example.com"))
            .isInstanceOf(AppException.class)
            .extracting("errorCode").isEqualTo(ErrorCode.INCORRECT_LOGIN);
    }

    @Test
    void loadUserByUsername_WithRoles_ReturnsAuthorities() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        Role role = new Role();
        role.setName("admin");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername("user@example.com");
        assertThat(result.getAuthorities()).isNotEmpty();
        assertThat(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_admin"))).isTrue();
    }

    @Test
    void loadUserByUsername_WithPermissions_ReturnsAuthorities() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        Role role = new Role();
        role.setName("user");
        Permission permission = new Permission();
        permission.setName("READ");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);
        role.setPermissions(permissions);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername("user@example.com");
        assertThat(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("READ"))).isTrue();
    }

    @Test
    void loadUserByUsername_WithMultipleRoles_ReturnsAllAuthorities() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        Role role1 = new Role();
        role1.setName("admin");
        Role role2 = new Role();
        role2.setName("user");
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        user.setRoles(roles);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername("user@example.com");
        assertThat(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_admin"))).isTrue();
        assertThat(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_user"))).isTrue();
    }

    @Test
    void loadUserByUsername_WithBlankRoleName_SkipsRole() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        Role role = new Role();
        role.setName("");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername("user@example.com");
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void loadUserByUsername_WithNullPermissionName_SkipsPermission() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        Role role = new Role();
        role.setName("user");
        Permission permission = new Permission();
        permission.setName(null);
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);
        role.setPermissions(permissions);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername("user@example.com");
        assertThat(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_user"))).isTrue();
    }
}

