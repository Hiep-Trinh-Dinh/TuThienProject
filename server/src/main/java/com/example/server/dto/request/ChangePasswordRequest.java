package com.example.server.dto.request;

public record ChangePasswordRequest(String password, String repeatedPassword) {
}
