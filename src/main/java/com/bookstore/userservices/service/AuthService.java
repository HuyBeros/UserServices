package com.bookstore.userservices.service;

import com.bookstore.userservices.dto.request.LoginRequest;
import com.bookstore.userservices.dto.request.RefreshTokenRequest;
import com.bookstore.userservices.dto.request.RegisterRequest;
import com.bookstore.userservices.dto.response.AuthResponse;

/**
 * Giao diện định nghĩa các dịch vụ xác thực tài khoản và quản lý phiên làm việc.
 * Cung cấp chức năng: đăng ký khách hàng, đăng nhập hệ thống và cấp mới token.
 */
public interface AuthService {

    /**
     * Đăng ký tài khoản khách hàng mới vào hệ thống.
     *
     * @param request thông tin đăng ký tài khoản (tên đăng nhập, mật khẩu, họ tên, email, ...)
     * @return kết quả xác thực gồm Access Token, Refresh Token và thông tin cơ bản của người dùng
     * @throws com.bookstore.userservices.exception.AppException nếu tên đăng nhập hoặc email đã tồn tại
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Đăng nhập vào hệ thống bằng tên đăng nhập hoặc email và mật khẩu.
     *
     * @param request thông tin đăng nhập gồm username/email và mật khẩu
     * @return kết quả xác thực gồm Access Token, Refresh Token và thông tin cơ bản của người dùng
     * @throws com.bookstore.userservices.exception.AppException nếu vượt giới hạn đăng nhập, thông tin xác thực sai hoặc tài khoản bị khóa
     */
    AuthResponse login(LoginRequest request);

    /**
     * Cấp phát cặp Access Token và Refresh Token mới thông qua Refresh Token hợp lệ.
     *
     * @param request yêu cầu chứa chuỗi refresh token hợp lệ
     * @return kết quả xác thực với cặp token mới
     * @throws com.bookstore.userservices.exception.AppException nếu token không hợp lệ, hết hạn hoặc tài khoản bị khóa
     */
    AuthResponse refreshToken(RefreshTokenRequest request);
}
