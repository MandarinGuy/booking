package org.mandarin.booking.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;

class NullableQueryFilterBuilderTest {

    @Test
    void whenHasText_null_shouldIgnore() {
        var result = NullableQueryFilterBuilder.builder()
                .whenHasText(null, s -> fail())
                .build();

        assertThat(result).isEqualTo(new BooleanBuilder());
    }

    @Test
    void whenHasText_empty_shouldIgnore() {
        var result = NullableQueryFilterBuilder.builder()
                .whenHasText("", s -> fail())
                .build();

        assertThat(result).isEqualTo(new BooleanBuilder());
    }

    @Test
    void whenHasText_blank_shouldIgnore() {
        var result = NullableQueryFilterBuilder.builder()
                .whenHasText("   ", s -> fail())
                .build();

        assertThat(result).isEqualTo(new BooleanBuilder());
    }

}
