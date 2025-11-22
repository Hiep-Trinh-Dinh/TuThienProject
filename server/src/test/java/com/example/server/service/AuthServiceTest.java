package com.example.server.service;

import com.example.server.dto.request.IntrospectRequest;
import com.example.server.dto.request.LoginRequestDTO;
import com.example.server.dto.request.LogoutRequest;
import com.example.server.dto.response.AuthResponse;
import com.example.server.dto.response.IntrospectResponse;
import com.example.server.entity.Role;
import com.example.server.entity.User;
import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.InvalidatedToken;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.InvalidatedTokenRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;
    @Mock private InvalidatedTokenRepository invalidatedTokenRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");

        User mockUser = new User();
        mockUser.setEmail(dto.getEmail());
        mockUser.setStatus(User.Status.ACTIVE);
        mockUser.setRoles(new HashSet<>());
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(roleRepository.findByName("user")).thenReturn(Optional.of(new Role()));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("token");

        AuthResponse res = authService.login(dto);
        assertThat(res.getToken()).isEqualTo("token");
    }

    @Test
    void login_incorrectPassword_throwsException() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("unknown@example.com");
        dto.setPassword("wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("failed!"));
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INCORRECT_LOGIN);
    }

    @Test
    void login_inactiveUser_throwsException() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("inactive@ex.com");
        dto.setPassword("pw");
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setStatus(User.Status.INACTIVE);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void login_addsDefaultRoleIfNotExist() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("abc@ab.com");
        dto.setPassword("p");
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setStatus(User.Status.ACTIVE);
        user.setRoles(null);

        Role defaultRole = new Role();
        defaultRole.setName("user");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findByName("user")).thenReturn(Optional.of(defaultRole));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("tok");
        when(userRepository.save(any())).thenReturn(user);

        AuthResponse res = authService.login(dto);
        assertThat(res).isNotNull();
        assertThat(user.getRoles()).isNotNull();
    }

    @Test
    void introspect_validToken_returnsTrue() {
        String tok = "tok";
        given(jwtUtil.validateToken(tok)).willReturn(true);
        given(jwtUtil.extractAllClaims(tok)).willReturn(new io.jsonwebtoken.impl.DefaultClaims(Map.of("exp", new Date(System.currentTimeMillis()+1000000))));
        IntrospectRequest req = new IntrospectRequest();
        req.setToken(tok);
        IntrospectResponse res = authService.introspect(req);
        assertThat(res.isValid()).isTrue();
    }

    @Test
    void introspect_invalidToken_returnsFalse() {
        String tok = "badtoken";
        given(jwtUtil.validateToken(tok)).willReturn(false);
        IntrospectRequest req = new IntrospectRequest();
        req.setToken(tok);
        IntrospectResponse res = authService.introspect(req);
        assertThat(res.isValid()).isFalse();
    }

    @Test
    void logout_success_savesInvalidatedToken() {
        String tokenStr = "token";
        LogoutRequest req = new LogoutRequest();
        req.setToken(tokenStr);
        var claims = mock(io.jsonwebtoken.Claims.class);
        given(jwtUtil.extractAllClaims(tokenStr)).willReturn(claims);
        given(jwtUtil.validateToken(tokenStr)).willReturn(true);
        given(claims.getExpiration()).willReturn(new Date(System.currentTimeMillis()+1000000));
        given(claims.getId()).willReturn("someid");
        given(invalidatedTokenRepository.existsById("someid")).willReturn(false);
        authService.logout(req);
        verify(invalidatedTokenRepository).save(any(InvalidatedToken.class));
    }

    @Test
    void logout_expiredToken_catchAndDoNothing() {
        String tokenStr = "expired";
        LogoutRequest req = new LogoutRequest();
        req.setToken(tokenStr);
        when(jwtUtil.extractAllClaims(tokenStr)).thenThrow(new RuntimeException("Expired!"));
        assertThatCode(() -> authService.logout(req)).doesNotThrowAnyException();
    }

    @Test
    void introspect_expiredToken_returnsFalse() {
        String tok = "expired";
        given(jwtUtil.validateToken(tok)).willReturn(true);
        io.jsonwebtoken.Claims claims = new io.jsonwebtoken.impl.DefaultClaims();
        claims.setExpiration(new Date(System.currentTimeMillis() - 1000));
        given(jwtUtil.extractAllClaims(tok)).willReturn(claims);
        IntrospectRequest req = new IntrospectRequest();
        req.setToken(tok);
        IntrospectResponse res = authService.introspect(req);
        assertThat(res.isValid()).isFalse();
    }

    @Test
    void logout_tokenAlreadyExists_doesNotSaveAgain() {
        String tokenStr = "token";
        LogoutRequest req = new LogoutRequest();
        req.setToken(tokenStr);
        var claims = mock(io.jsonwebtoken.Claims.class);
        given(jwtUtil.extractAllClaims(tokenStr)).willReturn(claims);
        given(jwtUtil.validateToken(tokenStr)).willReturn(true);
        given(claims.getExpiration()).willReturn(new Date(System.currentTimeMillis()+1000000));
        given(claims.getId()).willReturn("someid");
        given(invalidatedTokenRepository.existsById("someid")).willReturn(true);
        authService.logout(req);
        verify(invalidatedTokenRepository, never()).save(any(InvalidatedToken.class));
    }


    @Test
    void login_userNotFound_throwsException() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("notfound@example.com");
        dto.setPassword("password");
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(dto))
            .isInstanceOf(AppException.class)
            .extracting("errorCode").isEqualTo(ErrorCode.INCORRECT_LOGIN);
    }

    @Test
    void login_createDefaultRoleIfNotExists() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password");
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setStatus(User.Status.ACTIVE);
        user.setRoles(null);
        Role newRole = new Role();
        newRole.setName("user");
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findByName("user")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("token");
        when(userRepository.save(any(User.class))).thenReturn(user);
        AuthResponse res = authService.login(dto);
        assertThat(res).isNotNull();
        verify(roleRepository).save(any(Role.class));
    }
}
