package com.example.server.controller;

import com.example.server.dto.request.PasswordRequestDTO;
import com.example.server.dto.request.RegisterRequestDTO;
import com.example.server.dto.request.UpdateUserInfoRequestDTO;
import com.example.server.dto.request.UserUpdateRequest;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.UserResponse;
import com.example.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;

    // dang ky tao tai khoan
    @PostMapping("/register")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody RegisterRequestDTO request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    // cap nhat user qua id do user thao tac
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUserById(@PathVariable Long id,@Valid @RequestBody UpdateUserInfoRequestDTO request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUserById(id, request));
        return apiResponse;
    }

    // cap nhat day du thong tin cua user do admin thao tac
    @PutMapping("/user/{id}")
    public ApiResponse<UserResponse> updateFullUserInfoById(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateFullUserInfoById(request, id));
        return apiResponse;
    }

    // thay doi mat khau
    @PatchMapping("/password/{id}")
    public ApiResponse<UserResponse> changPwdById(@PathVariable Long id, @Valid @RequestBody PasswordRequestDTO dto) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.changePwdById(id, dto));
        return apiResponse;
    }

    // admin: cập nhật trạng thái user (ACTIVE/INACTIVE/BANNED)
    @PatchMapping("/user/{id}/status")
    public ApiResponse<UserResponse> updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.adminUpdateStatus(id, status));
        return apiResponse;
    }

    // lay danh sach tat ca users
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(){
        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getAllUsers());
        return apiResponse;
    }

    // lay user qua email -- Đổi path cho an toàn
    @GetMapping("/email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserByEmail(email));
        return apiResponse;
    }

    // lay thong tin user dang nhap
    @GetMapping("/info")
    public ApiResponse<UserResponse> getUserInfo() {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserInfo());
        return apiResponse;
    }

    // xac nhan token gui qua email de enable new account
    @GetMapping("/register/confirmToken")
    public String confirmToken(@RequestParam("token") String token){
        userService.confirmToken(token);
        return "Confirmed token";
    }

}
