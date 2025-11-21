package com.example.server.service;

import com.example.server.dto.request.MailBodyRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "notification_queue")
    public void receiveNotify(MailBodyRequest message) {
        // Có thể bổ sung logging tại đây
        emailService.sendSimpleMessage(message);
    }
}
