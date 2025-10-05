package com.example.server.config;

import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.repository.PermissionRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
// khoi tao tai khoan admin neu trong db chua co
public class ApplicationInitConfig {
    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {

            if(!userRepository.existsByFullName("admin")){

                Role defaultRole = roleRepository.findByName("admin")
                        .orElseGet(() -> {
                            // Nếu role chưa tồn tại, tạo mới
                            Role newRole = new Role();
                            newRole.setName("admin");
                            newRole.setDescription("Default admin role");

                            return roleRepository.save(newRole);
                        });

                Set<Role> roles = new HashSet<>();
                roles.add(defaultRole);

                User user = User.builder()
                        .email("admin@gmail.com")
                        .fullName("admin")
                        .passwordHash(passwordEncoder.encode("admin"))
                        .status(User.Status.active)
                        .roles(roles)
                        .authProvider(AuthenticationProvider.LOCAL)
                        .build();

                userRepository.save(user);
                log.warn("default admin user has been created with default password: admin, please change it.");
            }
        };
    }
}
