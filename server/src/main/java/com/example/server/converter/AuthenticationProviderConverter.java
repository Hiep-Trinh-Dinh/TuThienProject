package com.example.server.converter;

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
        return attribute.name();
    }

    @Override
    public AuthenticationProvider convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            // Thử parse với chữ hoa trước (chuẩn)
            return AuthenticationProvider.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu không tìm thấy, thử với chữ thường hoặc mixed case
            for (AuthenticationProvider provider : AuthenticationProvider.values()) {
                if (provider.name().equalsIgnoreCase(dbData)) {
                    return provider;
                }
            }
            throw new IllegalArgumentException("No enum constant for: " + dbData);
        }
    }
}

