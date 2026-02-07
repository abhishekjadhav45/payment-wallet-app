package com.abhi.wallet.controller;

import com.abhi.wallet.dto.LoginRequest;
import com.abhi.wallet.dto.LoginResponse;
import com.abhi.wallet.dto.RegisterRequest;
import com.abhi.wallet.entity.User;
import com.abhi.wallet.security.CustomUserDetailsService;
import com.abhi.wallet.security.JwtUtil;
import com.abhi.wallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtUtil.generateToken(
                    userDetailsService.loadUserByUsername(request.getEmail())
            );

            return ResponseEntity.ok(new LoginResponse(token, "Login successful"));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, "Invalid email or password"));
        }
    }
}
