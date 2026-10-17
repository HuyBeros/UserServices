package com.bookstore.userservices.controller;

import com.bookstore.userservices.dto.request.LoginRequest;
import com.bookstore.userservices.dto.request.RefreshTokenRequest;
import com.bookstore.userservices.dto.request.RegisterRequest;
import com.bookstore.userservices.dto.response.ApiResponse;
import com.bookstore.userservices.dto.response.AuthResponse;
import com.bookstore.userservices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các yêu cầu liên quan đến xác thực người dùng.
 * Bao gồm: Đăng ký tài khoản mới, Đăng nhập và Cấp mới Access Token qua Refresh Token.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint đăng ký tài khoản khách hàng mới.
     *
     * @param request dữ liệu đăng ký hợp lệ từ người dùng
     * @return HTTP 201 Created kèm ApiResponse chứa thông tin tài khoản và cặp token
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đăng ký tài khoản thành công"));
    }

    /**
     * Endpoint đăng nhập vào hệ thống.
     *
     * @param request thông tin đăng nhập gồm username/email và mật khẩu
     * @return HTTP 200 OK kèm ApiResponse chứa thông tin đăng nhập và token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công"));
    }

    /**
     * Endpoint làm mới Access Token bằng Refresh Token.
     *
     * @param request dữ liệu chứa refresh token còn hạn
     * @return HTTP 200 OK kèm cặp token mới
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Làm mới token thành công"));
    }
}
