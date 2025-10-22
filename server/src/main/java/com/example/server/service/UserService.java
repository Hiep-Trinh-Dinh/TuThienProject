package com.example.server.service;

import com.example.server.dto.request.*;
import com.example.server.dto.response.UserResponse;
import com.example.server.entity.AuthenticationProvider;
import com.example.server.entity.ConfirmationToken;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.mapper.UserMapper;
import com.example.server.repository.ConfirmationTokenRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;
    ConfirmationTokenRepository confirmationTokenRepository;
    EmailService emailService;

    public UserResponse createUser(RegisterRequestDTO request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        user = userRepository.save(user);

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        confirmationTokenRepository.save(confirmationToken);

        String messageBody = """
               \s
                Thank you for registration. Please confirm your email.
               \s
                http://localhost:8080/api/accounts/register/confirmToken?token=%s
                   \s
               \s""".formatted(confirmationToken.getToken());

        MailBodyRequest mailBodyRequest = MailBodyRequest.builder()
                .to(user.getEmail())
                .text(messageBody)
                .subject("Confirm your email")
                .build();
        emailService.sendSimpleMessage(mailBodyRequest);

        return userMapper.toUserResponse(user);
    }

    public void confirmToken(String token){
        // 1. check if the token exist
        ConfirmationToken confirmedToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(()->new AppException(ErrorCode.TOKEN_NOT_FOUND));
        // 2. check if user already verified
        if(confirmedToken.getConfirmedAt() != null){
            throw new IllegalStateException("User already verified");
        }
        // 3. check if token expired
        if(confirmedToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token expired");
        }
        // 4. if everything is ok then update the confirmation time
        confirmedToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmedToken);
        // 5. enable the user
        enableUser(confirmedToken.getUser());
    }

    private void enableUser(User user) {
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
    }

    // this update info function is designed for USER
    @PreAuthorize("hasRole('user')")
    public UserResponse updateUserById(Long id, UpdateUserInfoRequestDTO user){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getFullName() != null) existing.setFullName(user.getFullName());
        if (user.getPhone() != null) {
            String phone = user.getPhone().trim();
            // Regex: bắt đầu bằng 0, dài 10–11 chữ số
            if (!phone.matches("^0[0-9]{9,10}$")) {
                throw new AppException(ErrorCode.PHONE_INVALID);
            }
            existing.setPhone(phone);
        }
        return userMapper.toUserResponse(userRepository.save(existing));
    }

    // this updating info function is designed for admin
    @PreAuthorize("hasRole('admin')")
    public UserResponse updateFullUserInfoById(UserUpdateRequest request, Long id){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (request.getFullName() != null) existing.setFullName(request.getFullName());
        if (request.getPhone() != null) {
            String phone = request.getPhone().trim();
            // Regex: bắt đầu bằng 0, dài 10–11 chữ số
            if (!phone.matches("^0[0-9]{9,10}$")) {
                throw new AppException(ErrorCode.PHONE_INVALID);
            }
            existing.setPhone(phone);
        }
        if(!CollectionUtils.isEmpty(request.getRoles())){
            var roles = roleRepository.findAllById(request.getRoles());
            existing.setRoles(new HashSet<>(roles));
        }
        existing = userRepository.save(existing);
        return userMapper.toUserResponse(existing);
    }

    @PreAuthorize("hasRole('user')")
    public UserResponse changePwdById(Long id, PasswordRequestDTO pwd){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (passwordEncoder.matches(pwd.getCurrentPassword(), existing.getPasswordHash())){
            existing.setPasswordHash(passwordEncoder.encode(pwd.getNewPassword()));
        }else{
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }
        return userMapper.toUserResponse(userRepository.save(existing));
    }

    @PreAuthorize("hasRole('admin')")
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PreAuthorize("hasRole('admin')")
    public UserResponse getUserByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserInfo(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .authProvider(String.valueOf(user.getAuthProvider()))
                .roles(null)
                .build();
    }

    public void updateAuthProvider(String email, String authProviderName){
        AuthenticationProvider authType = AuthenticationProvider.valueOf(authProviderName.toUpperCase());
        User existing = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        existing.setAuthProvider(authType);
        userRepository.save(existing);
    }
}
