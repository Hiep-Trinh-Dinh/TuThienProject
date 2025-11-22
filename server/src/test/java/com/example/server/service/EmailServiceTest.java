package com.example.server.service;

import com.example.server.dto.request.MailBodyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {
    @Mock JavaMailSender javaMailSender;
    @InjectMocks EmailService emailService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        java.lang.reflect.Field field = EmailService.class.getDeclaredField("mailUserName");
        field.setAccessible(true);
        field.set(emailService, "test@host.com");
    }

    @Test
    void sendSimpleMessage_callsSenderWithCorrectData() {
        MailBodyRequest mailBody = MailBodyRequest.builder()
            .to("abc@def.com")
            .subject("Tiêu đề")
            .text("Nội dung")
            .build();
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        emailService.sendSimpleMessage(mailBody);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleMessage_withLongText_sendsSuccessfully() {
        MailBodyRequest mailBody = MailBodyRequest.builder()
            .to("test@example.com")
            .subject("Long Subject")
            .text("This is a very long email content that should be sent successfully without any issues.")
            .build();
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        emailService.sendSimpleMessage(mailBody);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleMessage_withSpecialCharacters_sendsSuccessfully() {
        MailBodyRequest mailBody = MailBodyRequest.builder()
            .to("test@example.com")
            .subject("Test với ký tự đặc biệt: !@#$%")
            .text("Nội dung email với ký tự đặc biệt: á, é, í, ó, ú")
            .build();
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        emailService.sendSimpleMessage(mailBody);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
