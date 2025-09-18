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
        if (user.getPhone() != null) {
            String phone = user.getPhone().trim();
            // Regex: bắt đầu bằng 0, dài 10–11 chữ số
            if (!phone.matches("^0[0-9]{9,10}$")) {
                throw new IllegalArgumentException("Invalid phone number format. Must start with 0 and have 10–11 digits.");
            }
            existing.setPhone(phone);
        }
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
