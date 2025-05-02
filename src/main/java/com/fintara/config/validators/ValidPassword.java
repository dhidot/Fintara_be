package com.fintara.config.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password tidak memenuhi kriteria";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
