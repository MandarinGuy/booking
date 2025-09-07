package org.mandarin.booking.domain.show;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ShowScheduleTest {

    static Stream<Arguments> cases() {
        var bStart = LocalDateTime.of(2025, 9, 10, 19, 0);
        var bEnd = LocalDateTime.of(2025, 9, 10, 21, 0);
        return Stream.of(
                // True cases
                Arguments.of("exact same", bStart, bEnd, bStart, bEnd, true),
                Arguments.of("contained within", bStart, bEnd, bStart.plusMinutes(30), bEnd.minusMinutes(30), true),
                Arguments.of("containing (wraps around)", bStart, bEnd, bStart.minusMinutes(15), bEnd.plusMinutes(15),
                        true),
                Arguments.of("overlap at start (ends inside)", bStart, bEnd, bStart.minusMinutes(30),
                        bStart.plusMinutes(30), true),
                Arguments.of("overlap at end (starts inside)", bStart, bEnd, bEnd.minusMinutes(30),
                        bEnd.plusMinutes(30), true),

                // False cases
                Arguments.of("touching before (end equals base.start)", bStart, bEnd, bStart.minusHours(2), bStart,
                        false),
                Arguments.of("touching after (start equals base.end)", bStart, bEnd, bEnd, bEnd.plusHours(2), false),
                Arguments.of("completely before", bStart, bEnd, bStart.minusHours(3), bStart.minusHours(1), false),
                Arguments.of("completely after", bStart, bEnd, bEnd.plusHours(1), bEnd.plusHours(3), false)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void isConflict(
            String name,
            LocalDateTime baseStart,
            LocalDateTime baseEnd,
            LocalDateTime targetStart,
            LocalDateTime targetEnd,
            boolean expected
    ) {
        // Arrange
        var base = ShowSchedule.create(null, null,
                new ShowSchedule.ShowScheduleCreateCommand(1L, baseStart, baseEnd));

        // Act
        boolean actual = base.isConflict(targetStart, targetEnd);

        // Assert
        assertThat(actual)
                .as("case '%s' should be %s", name, expected)
                .isEqualTo(expected);
    }
}
