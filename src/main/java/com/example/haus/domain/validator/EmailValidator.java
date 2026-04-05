package com.example.haus.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[A-Za-z0-9._%+-]{1,64}@" + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                    + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        if (value.length() > 254) {
            return false;
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }
}
