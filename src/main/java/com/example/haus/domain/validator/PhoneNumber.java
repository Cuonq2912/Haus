package com.example.haus.domain.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {

    String name();
    String message() default "{name} invalid data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
