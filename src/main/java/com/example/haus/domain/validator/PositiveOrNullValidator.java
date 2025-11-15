package com.example.haus.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveOrNullValidator implements ConstraintValidator<PositiveOrNull, Number> {
    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.doubleValue() > 0;
    }
}
