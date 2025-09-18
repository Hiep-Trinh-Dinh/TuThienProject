package com.example.server.controller;

import com.example.server.dto.PasswordDTO;
import com.example.server.dto.UpdateUserInfoRequestDTO;
import com.example.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;

    @PatchMapping("/user/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id,@Valid @RequestBody UpdateUserInfoRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUserById(id, dto));
    }

    @PatchMapping("/password/{id}")
    public ResponseEntity<?> changPwdById(@PathVariable Long id, @RequestBody PasswordDTO dto) {
        return ResponseEntity.ok(userService.changePwdById(id, dto));
    }

}
