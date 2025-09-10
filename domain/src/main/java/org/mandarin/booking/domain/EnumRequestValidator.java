package org.mandarin.booking.domain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.NullUnmarked;


@NullUnmarked
public class EnumRequestValidator implements ConstraintValidator<EnumRequest, String> {
    private Class<? extends Enum<?>> clazz;
    private String message;

    @Override
    public void initialize(EnumRequest constraintAnnotation) {
        clazz = constraintAnnotation.value();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        clazz.getEnumConstants();
        for (Enum<?> enumConstant : clazz.getEnumConstants()) {
            if (enumConstant.name().equals(s)) {
                return true;
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        return false;
    }
}
