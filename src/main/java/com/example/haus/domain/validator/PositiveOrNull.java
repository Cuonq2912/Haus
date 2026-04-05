package com.example.haus.domain.validator;

import com.example.haus.constant.ErrorMessage;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PositiveOrNullValidator.class)
public @interface PositiveOrNull {
    String message() default ErrorMessage.Validator.ERR_INPUT_CONSTRAINT_VALIDATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
