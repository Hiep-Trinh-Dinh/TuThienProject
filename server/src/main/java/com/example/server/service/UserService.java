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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Slf4j
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

    public List<UserResponse> getAllUsers(){
        try {
            List<User> users = userRepository.findAll();
            log.info("Found {} users from database", users.size());
            
            if (users.isEmpty()) {
                log.warn("No users found in database");
                return List.of();
            }
            
            return users.stream()
                    .map(user -> {
                        log.debug("Mapping user: {} with roles: {}, authProvider: {}", 
                                user.getEmail(), 
                                user.getRoles() != null ? user.getRoles().size() : 0,
                                user.getAuthProvider());
                        // Đảm bảo authProvider không null trước khi map
                        // Nếu có lỗi parse enum từ DB, converter đã xử lý
                        if (user.getAuthProvider() == null) {
                            user.setAuthProvider(AuthenticationProvider.LOCAL);
                        }
                        try {
                            UserResponse response = userMapper.toUserResponse(user);
                            log.debug("Mapped user response: userId={}, email={}, rolesCount={}", 
                                    response.getUserId(), response.getEmail(),
                                    response.getRoles() != null ? response.getRoles().size() : 0);
                            return response;
                        } catch (Exception e) {
                            log.error("Error mapping user {}: {}", user.getEmail(), e.getMessage(), e);
                            throw new RuntimeException("Error mapping user " + user.getEmail() + ": " + e.getMessage(), e);
                        }
                    })
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Error retrieving users: " + e.getMessage(), e);
        }
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

<<<<<<< HEAD
        // Sử dụng mapper để trả về đầy đủ thông tin bao gồm roles
=======
>>>>>>> 59ed0194d068c44925bf23a0a2c452e9d3cfbe99
        return userMapper.toUserResponse(user);
    }

    public void updateAuthProvider(String email, String authProviderName){
        // Nếu null hoặc empty thì không cập nhật
        if(authProviderName == null || authProviderName.isBlank()) return;
        AuthenticationProvider authType;
        try {
            authType = AuthenticationProvider.valueOf(authProviderName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu không đúng giá trị enum thì không cập nhật
            return;
        }
        User existing = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        existing.setAuthProvider(authType);
        userRepository.save(existing);
    }

    @PreAuthorize("hasRole('admin')")
<<<<<<< HEAD
    public UserResponse adminUpdateStatus(Long id, String status){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        try {
            User.Status newStatus = User.Status.valueOf(status);
            existing.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_KEY);
=======
    public UserResponse updateUserStatus(Long id, String status) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        try {
            existing.setStatus(User.Status.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_PARAM);
>>>>>>> 59ed0194d068c44925bf23a0a2c452e9d3cfbe99
        }
        existing = userRepository.save(existing);
        return userMapper.toUserResponse(existing);
    }
}
