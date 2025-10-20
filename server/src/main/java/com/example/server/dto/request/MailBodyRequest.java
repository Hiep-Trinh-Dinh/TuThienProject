package com.example.server.dto.request;

import lombok.Builder;

@Builder
public record MailBodyRequest(String to, String subject, String text) {
}
