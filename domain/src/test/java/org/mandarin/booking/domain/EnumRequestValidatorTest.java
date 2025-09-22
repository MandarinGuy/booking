package org.mandarin.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.lang.reflect.Field;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnumRequestValidatorBranchTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void nullAllowed_passes() {
        var dto = new DtoNullableTrue(null);
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void nullDenied_fails_withDefaultMessage() {
        var dto = new DtoNullableFalse(null);
        Set<ConstraintViolation<DtoNullableFalse>> v = validator.validate(dto);
        assertThat(v).hasSize(1);

        assertThat(v.iterator().next().getMessage())
                .isEqualTo("커스텀-불일치");
    }

    @Test
    void match_passes() {
        assertThat(validator.validate(new DtoNullableTrue("A"))).isEmpty();
        assertThat(validator.validate(new DtoNullableTrue("MUSICAL"))).isEmpty();
    }

    @Test
    void mismatch_fails_withCustomViolation() {
        Set<ConstraintViolation<DtoNullableTrue>> v = validator.validate(new DtoNullableTrue("musical"));
        assertThat(v).hasSize(1);
        assertThat(v.iterator().next().getMessage()).isEqualTo("커스텀-불일치");
    }

    @Test
    void constantsIsNull_branch() throws Exception {
        EnumRequestValidator validator = new EnumRequestValidator();

        setField(validator, "clazz", String.class);
        setField(validator, "message", "ENUM 값이 올바르지 않습니다.");
        setField(validator, "nullable", false);

        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(ctx.buildConstraintViolationWithTemplate("ENUM 값이 올바르지 않습니다."))
                .thenReturn(builder);

        boolean result = validator.isValid("ANY", ctx);

        assertThat(result).isFalse();
        verify(ctx).disableDefaultConstraintViolation();
        verify(ctx).buildConstraintViolationWithTemplate("ENUM 값이 올바르지 않습니다.");
        verify(builder).addConstraintViolation();
        verifyNoMoreInteractions(ctx, builder);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    enum Sample {A, MUSICAL}

    static class DtoNullableTrue {
        @EnumRequest(value = Sample.class, nullable = true, message = "커스텀-불일치")
        String v;

        DtoNullableTrue(String v) {
            this.v = v;
        }
    }

    static class DtoNullableFalse {
        @EnumRequest(value = Sample.class, message = "커스텀-불일치")
        String v;

        DtoNullableFalse(String v) {
            this.v = v;
        }
    }
}
