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
    // Có thể viết thêm test cho updateUserById, updateUserAvatar ...
}
