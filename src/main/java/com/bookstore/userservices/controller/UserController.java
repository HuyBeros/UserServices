package com.bookstore.userservices.controller;

import com.bookstore.userservices.dto.request.ChangePasswordRequest;
import com.bookstore.userservices.dto.request.UpdateProfileRequest;
import com.bookstore.userservices.dto.response.ApiResponse;
import com.bookstore.userservices.dto.response.UserDetailResponse;
import com.bookstore.userservices.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các yêu cầu liên quan đến hồ sơ của người dùng đang đăng nhập.
 * Danh tính người dùng được trích xuất an toàn từ JWT Token qua annotation @AuthenticationPrincipal.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "2. User Profile", description = "Các API thông tin cá nhân của người dùng đang đăng nhập")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint lấy thông tin cá nhân của người dùng hiện tại.
     *
     * @param userDetails thông tin xác thực của user lấy từ SecurityContext
     * @return HTTP 200 OK kèm DTO chi tiết người dùng
     */
    @Operation(summary = "Lấy thông tin cá nhân", description = "Lấy hồ sơ cá nhân của user đang đăng nhập dựa trên JWT token")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserDetailResponse response = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin tài khoản thành công"));
    }

    /**
     * Endpoint cập nhật thông tin hồ sơ người dùng.
     *
     * @param userDetails thông tin xác thực của user
     * @param request dữ liệu cập nhật (họ tên, email, sđt, địa chỉ)
     * @return HTTP 200 OK kèm DTO người dùng sau khi cập nhật
     */
    @Operation(summary = "Cập nhật hồ sơ cá nhân", description = "Cập nhật họ tên, email, số điện thoại, địa chỉ của user")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserDetailResponse response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thông tin tài khoản thành công"));
    }

    /**
     * Endpoint đổi mật khẩu tài khoản người dùng.
     *
     * @param userDetails thông tin xác thực của user
     * @param request dữ liệu đổi mật khẩu gồm mật khẩu cũ và mới
     * @return HTTP 200 OK xác nhận đổi mật khẩu thành công
     */
    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu cá nhân (yêu cầu cung cấp đúng mật khẩu cũ)")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công"));
    }
}
