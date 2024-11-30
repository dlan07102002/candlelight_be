package vn.duclan.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD}) // apply for only variable
@Retention(RetentionPolicy.RUNTIME) // Define when this annotation is executed.
@Constraint(validatedBy = PasswordValidator.class) // Link to the validation logic
public @interface PasswordConstraints {
    String message() default
            "Password must have at least {min} characters, including one uppercase letter, one special character, and one number.";

    Class<?>[] groups() default {};

    int min() default 8;

    Class<? extends Payload>[] payload() default {};
}
