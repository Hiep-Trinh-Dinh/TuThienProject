package com.example.server.service;

import com.example.server.dto.AuthResponse;
import com.example.server.dto.LoginRequestDTO;
import com.example.server.dto.RegisterRequestDTO;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequestDTO registerRequestDTO){
//        User user = User.builder()
//                .fullName(registerRequestDTO.getFullName())
//                .email(registerRequestDTO.getEmail())
//                .passwordHash(passwordEncoder.encode(registerRequestDTO.getPassword()))
//                .build();
        String email = registerRequestDTO.getEmail().trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if(userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email address already exists");
        }
        User user = new User();
        String hashed = passwordEncoder.encode(registerRequestDTO.getPassword());
        user.setPasswordHash(hashed);
        user.setFullName(registerRequestDTO.getFullName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhone());
        // Explicitly set default values
        user.setRole(User.Role.user);
        user.setStatus(User.Status.active);
        user.setCreatedAt(LocalDateTime.now());
        
        System.out.println("🔍 Creating user with email: " + user.getEmail());
        System.out.println("🔍 Password hash: " + user.getPasswordHash());
        System.out.println("🔍 Role: " + user.getRole());
        System.out.println("🔍 Status: " + user.getStatus());
        System.out.println("🔍 Created at: " + user.getCreatedAt());
        
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token,user);
    }

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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtil.generateToken(loginRequestDTO.getEmail());
        return new AuthResponse(token,user);
    }

}
