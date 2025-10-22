package com.example.haus.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveOrNullValidator implements ConstraintValidator<PositiveOrNull, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return value == null || value >= 0;
    }

}
