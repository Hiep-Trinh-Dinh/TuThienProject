package com.example.server.dto.request;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record MailBodyRequest(String to, String subject, String text) implements Serializable {
    private static final long serialVersionUID = 1L;
}
