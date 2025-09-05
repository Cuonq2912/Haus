package com.example.haus.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, CharSequence> {

    private List<String> acceptedValue;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        if (constraintAnnotation != null) {
            this.acceptedValue = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                    .map(Enum::name).toList();
        }
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return value != null && acceptedValue.contains(value.toString().toUpperCase());
    }
}
