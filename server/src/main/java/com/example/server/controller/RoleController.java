package com.example.server.controller;


import com.example.server.dto.request.RoleRequest;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.RoleResponse;
import com.example.server.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@CrossOrigin(origins = "http://localhost:5173")

public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.create(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll(){
        ApiResponse<List<RoleResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.getAll());
        return apiResponse;
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role){
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        roleService.delete(role);
        return apiResponse;
    }
}
