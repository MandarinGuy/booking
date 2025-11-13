package org.mandarin.booking.webapi.show.schedule.scheduleId.seat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatStream;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.show.SeatsResponse;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("GET /api/show/schedule/{scheduleId}/seat")
@IntegrationTest
class GET_specs {

    @Test
    void 유효한_scheduleId로_요청_시_200_OK와_contents_배열을_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var setup = testFixture.createScheduleSetup(
                LocalDate.of(2025, 10, 1), LocalDate.of(2025, 12, 31),
                LocalDateTime.of(2025, 10, 10, 19, 0), LocalDateTime.of(2025, 10, 10, 21, 30)
        );
        var scheduleId = testFixture.registerShowSchedule(setup);

        // Act
        var response = testUtils.get("/api/show/schedule/" + scheduleId + "/seat")
                .withAuthorization(testUtils.getAuthToken())
                .assertSuccess(SeatsResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
        var contents = response.getData().contents();
        assertThat(contents).isNotNull();
        assertThat(contents).isNotEmpty();
    }

    @Test
    void 각_요소는_seatId_gradeId_status_필드를_포함한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var setup = testFixture.createScheduleSetup(
                LocalDate.of(2025, 10, 1), LocalDate.of(2025, 12, 31),
                LocalDateTime.of(2025, 10, 10, 19, 0), LocalDateTime.of(2025, 10, 10, 21, 30)
        );
        var scheduleId = testFixture.registerShowSchedule(setup);

        // Act
        var response = testUtils.get("/api/show/schedule/" + scheduleId + "/seat")
                .withAuthorization(testUtils.getAuthToken())
                .assertSuccess(SeatsResponse.class);

        // Assert
        var contents = response.getData().contents();
        assertThatStream(contents.stream())
                .allSatisfy(res -> {
                    assertThat(res.seatId()).isNotNull();
                    assertThat(res.gradeId()).isNotNull();
                    assertThat(res.status()).isNotNull();
                });
    }
}
