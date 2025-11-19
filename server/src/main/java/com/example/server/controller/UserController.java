package com.example.server.controller;

import com.example.server.dto.request.PasswordRequestDTO;
import com.example.server.dto.request.RegisterRequestDTO;
import com.example.server.dto.request.UpdateUserInfoRequestDTO;
import com.example.server.dto.request.UserUpdateRequest;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.UserResponse;
import com.example.server.entity.User;
import com.example.server.service.FileStorageService;
import com.example.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;

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

    // cập nhật trạng thái user (lock/unlock)
    @PatchMapping("/user/{id}/status")
    public ApiResponse<UserResponse> updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUserStatus(id, status));
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

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            User updatedUser = userService.updateUserAvatar(userId, file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload avatar thành công");
            response.put("user", updatedUser);
            response.put("avatarUrl", updatedUser.getAvatarUrl());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Upload avatar thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{userId}/cover")
    public ResponseEntity<?> uploadCoverPhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            User updatedUser = userService.updateUserCoverPhoto(userId, file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload ảnh bìa thành công");
            response.put("user", updatedUser);
            response.put("coverUrl", updatedUser.getCoverPhotoUrl());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Upload ảnh bìa thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "type", defaultValue = "avatar") String type) {
        try {
            MultipartFile fileToUpload = file != null ? file : image;

            if (fileToUpload == null || fileToUpload.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("success", String.valueOf(false));
                errorResponse.put("error", "No file provided");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String fileUrl;

            switch (type.toLowerCase()) {
                case "avatar":
                    fileUrl = fileStorageService.storeUserAvatar(fileToUpload);
                    break;
                case "cover":
                    fileUrl = fileStorageService.storeUserCover(fileToUpload);
                    break;
                default:
                    fileUrl = fileStorageService.storeUserAvatar(fileToUpload);
            }

            Map<String, String> response = new HashMap<>();
            response.put("success", String.valueOf(true));
            response.put("message", "File uploaded successfully");
            response.put("url", fileUrl);
            response.put("type", type);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", String.valueOf(false));
            errorResponse.put("error", "Could not upload file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
