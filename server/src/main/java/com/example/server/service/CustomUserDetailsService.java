package com.example.server.service;

import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        System.out.println("🔍 Found user: " + user.getEmail());
        System.out.println("🔍 Roles: " + user.getRoles());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(buildAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private List<SimpleGrantedAuthority> buildAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                // Thêm quyền ROLE_
                if (role.getName() != null && !role.getName().isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                }

                // Thêm các permission
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        if (permission.getName() != null && !permission.getName().isBlank()) {
                            authorities.add(new SimpleGrantedAuthority(permission.getName()));
                        }
                    });
                }
            });
        }

        return authorities;
    }
}
