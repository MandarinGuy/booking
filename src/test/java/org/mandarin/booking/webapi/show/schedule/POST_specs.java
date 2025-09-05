package org.mandarin.booking.webapi.show.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/show/schedule")
public class POST_specs {

    @Test
    void 올바른_접근_토큰과_유효한_요청을_보내면_SUCCESS_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow();
        var reqeust = new ShowScheduleRegisterRequest(
                show.getId(),
                10L,
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30),
                150
        );

        // Act
        var response = testUtils.post("/api/show/schedule", reqeust)
                .withHeader("Authorization", testUtils.getAuthToken())
                .assertSuccess(ShowScheduleRegisterResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void 응답_본문에_scheduleId가_포함된다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow();
        var reqeust = new ShowScheduleRegisterRequest(
                show.getId(),
                10L,
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30),
                150
        );

        // Act
        var response = testUtils.post("/api/show/schedule", reqeust)
                .withHeader("Authorization", testUtils.getAuthToken())
                .assertSuccess(ShowScheduleRegisterResponse.class);

        // Assert
        assertThat(response.getData().scheduleId()).isNotNull();
    }
}
