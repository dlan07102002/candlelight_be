package vn.duclan.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// ConstraintValidator with 2 params (your annotation, datatype of your input field)
public class PasswordValidator implements ConstraintValidator<PasswordConstraints, String> {

    private int min; // Variable to hold the minimum length dynamically
    private String PASSWORD_PATTERN; // To store the dynamically generated pattern

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false; // Handle null case
        }
        return password.matches(PASSWORD_PATTERN);
    }

    @Override
    // Execute before isValid method to read param from annotation
    public void initialize(PasswordConstraints constraints) {
        ConstraintValidator.super.initialize(constraints);

        min = constraints.min();
        PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=.*\\d).{" + min + ",}$";
    }

}
