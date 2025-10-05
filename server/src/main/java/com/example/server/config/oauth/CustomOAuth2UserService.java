package com.example.server.config.oauth;

import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Role role = roleRepository.findByName("user")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("user");
                        newRole.setDescription("Default user role");
                        return roleRepository.save(newRole);
                    });

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setAuthProvider(AuthenticationProvider.GOOGLE);
            newUser.setRoles(Set.of(role));

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String dummyPassword = encoder.encode(UUID.randomUUID().toString()); // random password

            newUser.setPasswordHash(dummyPassword);

            return userRepository.save(newUser);
        });

        // Trả về custom user có cả thông tin role
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse("user");

        return new CustomOAuth2User(oAuth2User, roleName);
    }

}
