package com.example.server.controller;


import com.example.server.dto.request.PermissionRequest;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.PermissionResponse;
import com.example.server.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@CrossOrigin(origins = "http://localhost:5173")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
        ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(permissionService.create(request));
        return apiResponse;
    }

    @PutMapping
    ApiResponse<PermissionResponse> update(@RequestBody PermissionRequest request){
        ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(permissionService.updateDescription(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll(){
        ApiResponse<List<PermissionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(permissionService.getAll());
        return apiResponse;
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission){
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        permissionService.delete(permission);
        return apiResponse;
    }
}
