package com.example.server.service;

import com.example.server.dto.request.RegisterRequestDTO;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.mapper.UserMapper;
import com.example.server.repository.ConfirmationTokenRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserMapper userMapper;
    @Mock
    RoleRepository roleRepository;
    @Mock
    ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    EmailService emailService;
    @Mock
    FileStorageService fileStorageService;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_UserExisted_ThrowException() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("test@email.com");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));
        AppException ex = assertThrows(AppException.class, () -> userService.createUser(dto));
        assertEquals(ErrorCode.USER_EXISTED, ex.getErrorCode());
    }

    @Test
    void createUser_Success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("a@b.com");
        dto.setFullName("Nguyen Van A");
        dto.setPasswordHash("123456");
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedpw");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertDoesNotThrow(() -> userService.createUser(dto));
    }

    @Test
    void confirmToken_Success() {
        User user = new User();
        user.setStatus(User.Status.INACTIVE);
        com.example.server.entity.ConfirmationToken token = com.example.server.entity.ConfirmationToken.builder()
            .token("test-token")
            .user(user)
            .expiresAt(java.time.LocalDateTime.now().plusMinutes(15))
            .build();
        when(confirmationTokenRepository.findByToken("test-token")).thenReturn(Optional.of(token));
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertDoesNotThrow(() -> userService.confirmToken("test-token"));
    }

    @Test
    void confirmToken_TokenNotFound_ThrowsException() {
        when(confirmationTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        AppException ex = assertThrows(AppException.class, () -> userService.confirmToken("invalid"));
        assertEquals(ErrorCode.TOKEN_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void confirmToken_AlreadyVerified_ThrowsException() {
        User user = new User();
        com.example.server.entity.ConfirmationToken token = com.example.server.entity.ConfirmationToken.builder()
            .token("test-token")
            .user(user)
            .confirmedAt(java.time.LocalDateTime.now())
            .expiresAt(java.time.LocalDateTime.now().plusMinutes(15))
            .build();
        when(confirmationTokenRepository.findByToken("test-token")).thenReturn(Optional.of(token));
        assertThrows(IllegalStateException.class, () -> userService.confirmToken("test-token"));
    }

    @Test
    void confirmToken_Expired_ThrowsException() {
        User user = new User();
        com.example.server.entity.ConfirmationToken token = com.example.server.entity.ConfirmationToken.builder()
            .token("test-token")
            .user(user)
            .expiresAt(java.time.LocalDateTime.now().minusMinutes(1))
            .build();
        when(confirmationTokenRepository.findByToken("test-token")).thenReturn(Optional.of(token));
        assertThrows(IllegalStateException.class, () -> userService.confirmToken("test-token"));
    }

    @Test
    void updateUserById_UserNotFound_ThrowsException() {
        com.example.server.dto.request.UpdateUserInfoRequestDTO dto = new com.example.server.dto.request.UpdateUserInfoRequestDTO();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        AppException ex = assertThrows(AppException.class, () -> userService.updateUserById(999L, dto));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateUserById_InvalidPhone_ThrowsException() {
        User user = new User();
        com.example.server.dto.request.UpdateUserInfoRequestDTO dto = new com.example.server.dto.request.UpdateUserInfoRequestDTO();
        dto.setPhone("123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        AppException ex = assertThrows(AppException.class, () -> userService.updateUserById(1L, dto));
        assertEquals(ErrorCode.PHONE_INVALID, ex.getErrorCode());
    }

    @Test
    void updateUserById_ValidPhone_UpdatesUser() {
        User user = new User();
        com.example.server.dto.request.UpdateUserInfoRequestDTO dto = new com.example.server.dto.request.UpdateUserInfoRequestDTO();
        dto.setPhone("0123456789");
        dto.setFullName("New Name");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(new com.example.server.dto.response.UserResponse());
        assertDoesNotThrow(() -> userService.updateUserById(1L, dto));
    }
}
