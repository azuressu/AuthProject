package com.auth.presentation.controller;

import com.auth.application.aop.ValidateAdmin;
import com.auth.presentation.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/api/test")
    public ResponseEntity<ApiResponse> getTest() {
        return ResponseEntity.ok(new ApiResponse("USER"));
    }

    @ValidateAdmin(roles = {"ADMIN"})
    @GetMapping("/api/test/admin")
    public ResponseEntity<ApiResponse> getTestAdmin() {
        return ResponseEntity.ok(new ApiResponse("ADMIN"));
    }
    
}
