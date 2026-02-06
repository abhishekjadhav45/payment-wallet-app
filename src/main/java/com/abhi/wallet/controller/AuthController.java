package com.abhi.wallet.controller;

import com.abhi.wallet.dto.RegisterRequest;
import com.abhi.wallet.entity.User;
import com.abhi.wallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(user);
    }
}
