package com.abhi.wallet.controller;

import com.abhi.wallet.dto.AuthResponse;
import com.abhi.wallet.dto.LoginRequest;
import com.abhi.wallet.dto.RegisterRequest;
import com.abhi.wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok(new AuthResponse("user-registered-successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Simple login - just return token for Day 5 testing
        return ResponseEntity.ok(new AuthResponse("fake-jwt-token-for-testing"));
    }
}
