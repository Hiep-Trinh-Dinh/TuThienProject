package com.example.server.validator;

import com.example.server.dto.request.PasswordRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldsNotEqualValidator implements ConstraintValidator<FieldsNotEqual, PasswordRequestDTO> {

    @Override
    public void initialize(FieldsNotEqual constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PasswordRequestDTO value, ConstraintValidatorContext context) {
        if (value == null) return true;
        String currentPassword = value.getCurrentPassword();
        String newPassword = value.getNewPassword();

        if (currentPassword == null || newPassword == null) {
            return true;
        }

        return !currentPassword.equals(newPassword);
    }
}