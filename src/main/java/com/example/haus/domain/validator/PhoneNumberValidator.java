package com.example.haus.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    // 10 digits OR 123-456-7890 / 123.456.7890 / 123 456 7890 OR (123)-456-7890
    private static final Pattern PHONE_PATTERN = Pattern
            .compile("^(?:\\d{10}|\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4}|\\(\\d{3}\\)-\\d{3}-\\d{4})$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Thường để null hợp lệ và dùng @NotBlank/@NotNull để bắt buộc nhập
        if (value == null || value.isBlank()) {
            return true;
        }

        // Chặn input cực dài chống regex DoS
        if (value.length() > 32) {
            return false;
        }

        return PHONE_PATTERN.matcher(value).matches();
    }
}
