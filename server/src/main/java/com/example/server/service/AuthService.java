package com.example.server.service;

import com.example.server.dto.request.IntrospectRequest;
import com.example.server.dto.request.LogoutRequest;
import com.example.server.dto.response.AuthResponse;
import com.example.server.dto.request.LoginRequestDTO;
import com.example.server.dto.response.IntrospectResponse;
import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.InvalidatedToken;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.InvalidatedTokenRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthService {
    RoleRepository roleRepository;
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthResponse login(LoginRequestDTO loginRequestDTO){
        System.out.println(" Login attempt for email: " + loginRequestDTO.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),loginRequestDTO.getPassword())
            );
            System.out.println(" Authentication successful");
        } catch (Exception e) {
            System.out.println(" Authentication failed: " + e.getMessage());
            throw new AppException(ErrorCode.INCORRECT_LOGIN);
        }
        
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INCORRECT_LOGIN));

        if(user.getStatus().name().equals("INACTIVE")){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("user")
                    .orElseGet(() -> {
                        // Nếu role chưa tồn tại, tạo mới
                        Role newRole = new Role();
                        newRole.setName("user");
                        newRole.setDescription("Default user role");
                        return roleRepository.save(newRole);
                    });

            // Khởi tạo HashSet nếu roles là null
            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>());
            }
            user.getRoles().add(defaultRole);
            user.setAuthProvider(AuthenticationProvider.LOCAL);
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public IntrospectResponse introspect(IntrospectRequest request){
        boolean verified = jwtUtil.validateToken(request.getToken());

        if (!(verified && jwtUtil.extractAllClaims(request.getToken()).getExpiration().after(new Date()))){
            verified = false;
        }

        return IntrospectResponse.builder()
                .valid(verified)
                .build();
    }

    // đăng xuất và lưu token hết hạn vào db
    public void logout(LogoutRequest request) {
        try {
            var jwtToken = jwtUtil.extractAllClaims(request.getToken());
            if (!(jwtUtil.validateToken(request.getToken())&& jwtToken.getExpiration().after(new Date()))){
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jwtToken.getId())
                    .expiredTime(jwtToken.getExpiration())
                    .build();
            if(!invalidatedTokenRepository.existsById(jwtToken.getId())){
                invalidatedTokenRepository.save(invalidatedToken);
            }
        } catch (Exception e) {
            log.info("Token already expired!");
        }
    }
}
