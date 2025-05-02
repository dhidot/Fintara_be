package com.fintara.config.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            setMessage(context, "Password tidak boleh kosong");
            return false;
        }

        if (password.length() < 8) {
            setMessage(context, "Password minimal 8 karakter");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            setMessage(context, "Password harus mengandung huruf kapital (A-Z)");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            setMessage(context, "Password harus mengandung angka (0-9)");
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()].*")) {
            setMessage(context, "Password harus mengandung simbol (!@#$%^&*)");
            return false;
        }

        if (password.contains(" ")) {
            setMessage(context, "Password tidak boleh mengandung spasi");
            return false;
        }

        return true;
    }

    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation(); // Matikan default message
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation(); // Set pesan custom
    }
}
