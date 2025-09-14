package com.example.haus.domain.validator;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.user.Gender;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderSubsetValidator.class)
public @interface GenderSubset {

    Gender[] anyOf();
    String message() default ErrorMessage.Validator.ERR_GENDER_VALIDATOR;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
