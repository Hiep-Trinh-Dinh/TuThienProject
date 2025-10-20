package com.example.server.controller;

import com.example.server.dto.request.ChangePasswordRequest;
import com.example.server.dto.request.MailBodyRequest;
import com.example.server.dto.response.ApiResponse;
import com.example.server.entity.ForgotPassword;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ErrorCode;
import com.example.server.repository.ForgotPasswordRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/api/forgotPassword")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ForgotPasswordController {
    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;

    private final PasswordEncoder passwordEncoder;

    // send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<ApiResponse> verifyEmail(@PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));


        if (forgotPasswordRepository.existsByUser(user)){
            ForgotPassword existedForgotPassword = forgotPasswordRepository.findByUser(user)
                    .orElseThrow(()->new AppException(ErrorCode.INVALID_KEY));
            existedForgotPassword.getUser().setForgotPassword(null);
            forgotPasswordRepository.delete(existedForgotPassword);
        }

        int otp = otpGenerator();

        MailBodyRequest mailBodyRequest = MailBodyRequest.builder()
                .to(email)
                .text("This is OTP for your Forgot Password request .Please enter it soon before it expires in 60 seconds: "+ otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()+60*1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBodyRequest);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok(ApiResponse.builder()
                .message("Email sent for verification!")
                .build());
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<ApiResponse> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user)
                .orElseThrow(()-> new AppException(ErrorCode.INVALID_KEY));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            fp.getUser().setForgotPassword(null);
            forgotPasswordRepository.delete(fp);
            return new ResponseEntity<>(ApiResponse.builder().message("OTP has expired!").build()
                    , HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok(ApiResponse.builder().message("OTP verified!").build());
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<ApiResponse> changePasswordHandler(@RequestBody ChangePasswordRequest request,
                                                             @PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));

        if (forgotPasswordRepository.existsByUser(user)) {
            ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                    .orElseThrow(()->new AppException(ErrorCode.INVALID_KEY));

            if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
                fp.getUser().setForgotPassword(null);
                forgotPasswordRepository.delete(fp);
            }else{
                if (!Objects.equals(request.password(), request.repeatedPassword())) {
                    return new ResponseEntity<>(ApiResponse.builder().message("Please enter the password again!").build()
                            , HttpStatus.EXPECTATION_FAILED);
                }

                String encodedPassword = passwordEncoder.encode(request.password());
                userRepository.updatePassword(email,encodedPassword);
                return ResponseEntity.ok(ApiResponse.builder().message("Password has been changed!").build());
            }
        }
        return new ResponseEntity<>(ApiResponse.builder().message("Invalid Forgot Password Request!").build()
                , HttpStatus.EXPECTATION_FAILED);
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }
}
