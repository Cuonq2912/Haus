package com.example.haus.domain.validator;

import com.example.haus.constant.ErrorMessage;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface Email {

    String message() default ErrorMessage.Validator.ERR_EMAIL_VALIDATOR;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
