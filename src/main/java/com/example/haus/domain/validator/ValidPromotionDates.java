package com.example.haus.domain.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PromotionDateValidator.class)
@Documented
public @interface ValidPromotionDates {
    String message() default "Start date must be before end date OR min < max price order";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
