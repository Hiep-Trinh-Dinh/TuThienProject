package com.example.server.util;

import com.example.server.entity.AuthenticationProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AuthenticationProviderConverter implements AttributeConverter<AuthenticationProvider, String> {

    @Override
    public String convertToDatabaseColumn(AuthenticationProvider attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name(); // Lưu dưới dạng UPPERCASE
    }

    @Override
    public AuthenticationProvider convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            // Xử lý case-insensitive: chuyển sang uppercase trước khi parse
            return AuthenticationProvider.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu không tìm thấy enum, trả về LOCAL làm mặc định
            return AuthenticationProvider.LOCAL;
        }
    }
}

