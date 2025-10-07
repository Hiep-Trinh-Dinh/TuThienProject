package com.example.server.controller;

import com.example.server.dto.request.IntrospectRequest;
import com.example.server.dto.request.LogoutRequest;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.AuthResponse;
import com.example.server.dto.request.LoginRequestDTO;
import com.example.server.dto.request.RegisterRequestDTO;
import com.example.server.dto.response.IntrospectResponse;
import com.example.server.service.AuthService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;

    // đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }

    // kiểm tra tính hợp lệ của token
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        var res = authService.introspect(request);
        ApiResponse<IntrospectResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(res);
        return apiResponse;
    }

    // đăng xuất và hủy tính hợp lệ của token ngay khi chưa hết hạn
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
        authService.logout(request);
        return new ApiResponse<>();
    }

}
