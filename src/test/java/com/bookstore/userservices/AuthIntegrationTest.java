package com.bookstore.userservices;

import com.bookstore.userservices.dto.request.LoginRequest;
import com.bookstore.userservices.dto.request.RegisterRequest;
import com.bookstore.userservices.entity.Role;
import com.bookstore.userservices.entity.RoleName;
import com.bookstore.userservices.entity.User;
import com.bookstore.userservices.entity.UserStatus;
import com.bookstore.userservices.repository.RoleRepository;
import com.bookstore.userservices.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role customerRole;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName(RoleName.CUSTOMER)
                        .description("Khách hàng")
                        .build()));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser_" + uniqueSuffix)
                .password("Password@123")
                .fullName("Test User " + uniqueSuffix)
                .email("test_" + uniqueSuffix + "@example.com")
                .phone("0987654321")
                .address("Hà Nội")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("thành công")))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.username", is(request.getUsername())))
                .andExpect(jsonPath("$.data.role", is("CUSTOMER")));
    }

    @Test
    void testRegisterValidationFailure() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .username("") // Blank username
                .password("123") // Too short
                .email("not-an-email") // Invalid email
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.data.username", notNullValue()))
                .andExpect(jsonPath("$.data.password", notNullValue()))
                .andExpect(jsonPath("$.data.email", notNullValue()));
    }

    @Test
    void testLoginSuccess() throws Exception {
        String username = "login_success_" + System.currentTimeMillis();
        String password = "mypassword123";

        userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName("User Login Success")
                .email(username + "@example.com")
                .role(customerRole)
                .status(UserStatus.ACTIVE)
                .build());

        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail(username)
                .password(password)
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.username", is(username)));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        String username = "login_fail_" + System.currentTimeMillis();

        userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode("correctpass"))
                .fullName("User Login Fail")
                .email(username + "@example.com")
                .role(customerRole)
                .status(UserStatus.ACTIVE)
                .build());

        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail(username)
                .password("wrongpass")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tên đăng nhập hoặc mật khẩu không chính xác")));
    }

    @Test
    void testLoginLockedAccount() throws Exception {
        String username = "login_locked_" + System.currentTimeMillis();

        userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .fullName("User Locked")
                .email(username + "@example.com")
                .role(customerRole)
                .status(UserStatus.LOCKED)
                .build());

        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail(username)
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("bị khóa")));
    }
}
