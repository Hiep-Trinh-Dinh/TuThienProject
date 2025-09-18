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
        if(userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email address already exists");
        }
        User user = new User();
        String hashed = passwordEncoder.encode(registerRequestDTO.getPassword());
        user.setPasswordHash(hashed);
        user.setFullName(registerRequestDTO.getFullName());
        user.setEmail(registerRequestDTO.getEmail());
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token,user);
    }

    public AuthResponse login(LoginRequestDTO loginRequestDTO){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),loginRequestDTO.getPassword())
        );
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtil.generateToken(loginRequestDTO.getEmail());
        return new AuthResponse(token,user);
    }

}
