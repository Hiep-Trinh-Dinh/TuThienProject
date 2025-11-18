package com.example.server.config;

import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.Role;
import com.example.server.entity.User;
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

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
// khoi tao tai khoan admin neu trong db chua co
public class ApplicationInitConfig {
    String admin = "admin";
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {

            // Sử dụng existsByEmail để kiểm tra tài khoản theo email
            if(userRepository.findByEmail("admin@gmail.com").isEmpty()){

                Role defaultRole = roleRepository.findByName(admin)
                        .orElseGet(() -> {
                            // Nếu role chưa tồn tại, tạo mới
                            Role newRole = new Role();
                            newRole.setName(admin);
                            newRole.setDescription("Default admin role");
                            // Khởi tạo permissions để tránh LazyInitializationException
                            newRole.setPermissions(new HashSet<>());

                            return roleRepository.save(newRole);
                        });

                // Đảm bảo permissions được initialize để tránh LazyInitializationException
                if (defaultRole.getPermissions() == null) {
                    defaultRole.setPermissions(new HashSet<>());
                }

                Set<Role> roles = new HashSet<>();
                roles.add(defaultRole);

                User user = User.builder()
                        .email("admin@gmail.com")
                        .fullName(admin)
                        .passwordHash(passwordEncoder.encode(admin))
                        .status(User.Status.ACTIVE)
                        .roles(roles)
                        .authProvider(AuthenticationProvider.LOCAL)
                        .build();

                userRepository.save(user);
                log.warn("default admin user has been created with default password: admin, please change it.");
            }
        };
    }
}
