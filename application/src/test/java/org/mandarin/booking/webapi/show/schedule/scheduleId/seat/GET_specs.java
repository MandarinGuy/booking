package org.mandarin.booking.webapi.show.schedule.scheduleId.seat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;
import static org.mandarin.booking.utils.ShowFixture.generateShowScheduleRegisterRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.show.SeatsResponse;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("GET /api/show/schedule/{scheduleId}/seat")
class GET_specs {

    @Test
    void 유효한_scheduleId로_요청_시_SUCCESS와_contents_배열을_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 10, 1), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().orElseThrow();
        var gradeSeatMap = testFixture.generateGradeSeatMapByShowIdAndSectionId(show.getId(), sectionId);
        var request = generateShowScheduleRegisterRequest(
                show.getId(),
                sectionId,
                LocalDateTime.of(2025, 10, 10, 19, 0),
                LocalDateTime.of(2025, 10, 10, 21, 30),
                gradeSeatMap
        );

        var scheduleId = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertSuccess(ShowScheduleRegisterResponse.class)
                .getData()
                .scheduleId();

        // Act
        var response = testUtils.get("/api/show/schedule/" + scheduleId + "/seat")
                .withAuthorization(testUtils.getAuthToken())
                .assertSuccess(SeatsResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
        var contents = response.getData().contents();
        assertThat(contents).isNotNull();
        assertThat(contents).isNotEmpty();
        System.out.println("contents = " + contents);
    }
}
