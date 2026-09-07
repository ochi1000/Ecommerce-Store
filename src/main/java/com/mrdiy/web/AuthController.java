package com.mrdiy.web;

import com.mrdiy.common.ApiResponse;
import com.mrdiy.dto.AuthDtos;
import com.mrdiy.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        authService.register(request);
        return ApiResponse.ok("Registration successful", null);
    }

    @PostMapping("/auth")
    public ApiResponse<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request, false));
    }

    @PostMapping("/admin/auth")
    public ApiResponse<AuthDtos.LoginResponse> adminLogin(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request, true));
    }

    @PostMapping({"/user/logout", "/admin/logout"})
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ApiResponse.ok("Logout successful", null);
    }
}
