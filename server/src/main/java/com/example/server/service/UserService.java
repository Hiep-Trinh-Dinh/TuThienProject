package com.example.server.service;

import com.example.server.dto.PasswordDTO;
import com.example.server.dto.UpdateUserInfoRequestDTO;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User updateUserById(Long id, UpdateUserInfoRequestDTO user){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getFullName() != null) existing.setFullName(user.getFullName());
        if (user.getPhone() != null) existing.setPhone(user.getPhone());
        return userRepository.save(existing);
    }

    public User changePwdById(Long id, PasswordDTO pwd){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(pwd.getCurrentPassword(), existing.getPasswordHash())){
            existing.setPasswordHash(passwordEncoder.encode(pwd.getNewPassword()));
        }else{
            throw new RuntimeException("Current password is not correct");
        }
        return userRepository.save(existing);
    }
}
