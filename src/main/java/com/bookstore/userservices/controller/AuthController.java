package com.bookstore.userservices.controller;

import com.bookstore.userservices.dto.request.LoginRequest;
import com.bookstore.userservices.dto.request.RefreshTokenRequest;
import com.bookstore.userservices.dto.request.RegisterRequest;
import com.bookstore.userservices.dto.response.ApiResponse;
import com.bookstore.userservices.dto.response.AuthResponse;
import com.bookstore.userservices.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "1. Authentication", description = "Các API xác thực: Đăng ký, Đăng nhập & Refresh Token")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint đăng ký tài khoản khách hàng mới.
     *
     * @param request dữ liệu đăng ký hợp lệ từ người dùng
     * @return HTTP 201 Created kèm ApiResponse chứa thông tin tài khoản và cặp token
     */
    @Operation(summary = "Đăng ký tài khoản khách hàng mới", description = "Tạo mới tài khoản với vai trò CUSTOMER")
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
    @Operation(summary = "Đăng nhập hệ thống", description = "Đăng nhập bằng username/email và mật khẩu để nhận Bearer JWT Token")
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
    @Operation(summary = "Làm mới access token", description = "Sử dụng refresh token để lấy access token mới")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Làm mới token thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token không hợp lệ")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Làm mới token thành công"));
    }
}
