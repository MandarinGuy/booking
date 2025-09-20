package org.mandarin.booking.domain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

@NullUnmarked
public class EnumRequestValidator implements ConstraintValidator<EnumRequest, String> {
    private Class<? extends Enum<?>> clazz;
    private String message;
    private boolean nullable;

    @Override
    public void initialize(EnumRequest constraintAnnotation) {
        this.clazz = constraintAnnotation.value();
        this.message = constraintAnnotation.message();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        // nullable=true && 값이 null이면 검증 스킵
        if (value == null) {
            return nullable;
        }

        Enum<?>[] constants = clazz.getEnumConstants();
        if (constants != null) {
            for (Enum<?> e : constants) {
                if (e.name().equals(value)) {
                    return true;
                }
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
