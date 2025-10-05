package com.example.server.service;

import com.example.server.dto.request.IntrospectRequest;
import com.example.server.dto.request.LogoutRequest;
import com.example.server.dto.response.AuthResponse;
import com.example.server.dto.request.LoginRequestDTO;
import com.example.server.dto.request.RegisterRequestDTO;
import com.example.server.dto.response.IntrospectResponse;
import com.example.server.dto.response.RefreshResponse;
import com.example.server.entity.InvalidatedToken;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.InvalidatedTokenRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthService {
    private final RoleRepository roleRepository;

    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret}")
    protected String SIGNER_KEY;

    public AuthResponse login(LoginRequestDTO loginRequestDTO){
        System.out.println("🔍 Login attempt for email: " + loginRequestDTO.getEmail());
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),loginRequestDTO.getPassword())
            );
            System.out.println("✅ Authentication successful");
        } catch (Exception e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            throw e;
        }
        
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("user")
                    .orElseGet(() -> {
                        // Nếu role chưa tồn tại, tạo mới
                        Role newRole = new Role();
                        newRole.setName("user");
                        newRole.setDescription("Default user role");
                        return roleRepository.save(newRole);
                    });

            user.getRoles().add(defaultRole);
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public IntrospectResponse introspect(IntrospectRequest request){
        Boolean verified = jwtUtil.validateToken(request.getToken());

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
