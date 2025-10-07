package com.example.server.config.oauth;

import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.UserRepository;
import com.example.server.service.UserService;
import com.example.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User authUser = (CustomOAuth2User) authentication.getPrincipal();

        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_FOUND)
        );

        // update auth_provider
        userService.updateAuthProvider(authUser.getEmail(), authUser.getoAuth2ClientName());

        // Sinh JWT token
        String token = jwtUtil.generateToken(user);

        // Redirect về frontend
        response.sendRedirect("http://localhost:5173/?token=" + token);
    }
}
