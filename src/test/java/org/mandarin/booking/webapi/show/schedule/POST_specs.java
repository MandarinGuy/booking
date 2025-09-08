package org.mandarin.booking.webapi.show.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.FORBIDDEN;
import static org.mandarin.booking.adapter.webapi.ApiStatus.INTERNAL_SERVER_ERROR;
import static org.mandarin.booking.adapter.webapi.ApiStatus.NOT_FOUND;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.domain.member.MemberAuthority.DISTRIBUTOR;
import static org.mandarin.booking.domain.member.MemberAuthority.USER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.domain.show.Show;
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
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hall = testUtils.insertDummyHall();
        var request = generateShowScheduleRegisterRequest(
                show, hall.getId(),
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30));

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertSuccess(ShowScheduleRegisterResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void 응답_본문에_scheduleId가_포함된다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hall = testUtils.insertDummyHall();
        var request = generateShowScheduleRegisterRequest(
                show, hall.getId(),
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30));

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertSuccess(ShowScheduleRegisterResponse.class);

        // Assert
        assertThat(response.getData().scheduleId()).isNotNull();
    }

    @Test
    void 권한이_없는_사용자_토큰으로_요청하면_FORBIDDEN_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var request = generateShowScheduleRegisterRequest(show);

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(USER))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(FORBIDDEN);
    }


    @Test
    void startAt이_endAt보다_늦은_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var request = generateShowScheduleRegisterRequest(show,
                LocalDateTime.of(2025, 9, 10, 21, 30),
                LocalDateTime.of(2025, 9, 10, 19, 0), 10L
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getData()).contains("The end time must be after the start time");
    }

    @Test
    void 존재하지_않는_showId를_보내면_NOT_FOUND_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var request = new ShowScheduleRegisterRequest(
                9999L,// 존재하지 않는 showId
                10L,
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30)
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
        assertThat(response.getData()).contains("존재하지 않는 공연입니다.");
    }

    @Test
    void 존재하지_않는_hallId를_보내면_NOT_FOUND_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var request = new ShowScheduleRegisterRequest(
                show.getId(),
                9999L,// 존재하지 않는 hallId
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30)
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
        assertThat(response.getData()).contains("해당 공연장을 찾을 수 없습니다.");
    }

    @Test
    void 공연_기간_범위를_벗어나는_startAt_또는_endAt을_보낼_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var show = testUtils.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 11));
        var hall = testUtils.insertDummyHall();
        var request = new ShowScheduleRegisterRequest(
                show.getId(),
                hall.getId(),
                LocalDateTime.of(2023, 9, 10, 19, 0),
                LocalDateTime.of(2023, 9, 10, 21, 30)
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getData()).contains("공연 기간 범위를 벗어나는 일정입니다.");
    }

    @Test
    void 동일한_hallId와_시간이_겹치는_회차를_등록하려_하면_INTERNAL_SERVER_ERROR를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var hall = testUtils.insertDummyHall();

        var show = testUtils.insertDummyShow(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10)
        );
        var request = generateShowScheduleRegisterRequest(show, hall.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        var anotherShow = testUtils.insertDummyShow(
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(30)
        );
        var nextRequest = generateShowScheduleRegisterRequest(anotherShow, hall.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3)
        );

        testUtils.post(
                        "/api/show/schedule",
                        request
                )
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertSuccess(ShowScheduleRegisterResponse.class);

        // Act
        var response = testUtils.post(
                        "/api/show/schedule",
                        nextRequest
                )
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getData()).contains("해당 회차는 이미 공연 스케줄이 등록되어 있습니다.");
    }

    private ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show) {
        return generateShowScheduleRegisterRequest(show, LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30), 10L);
    }


    private ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show,
                                                                            long hallId,
                                                                            LocalDateTime startAt,
                                                                            LocalDateTime endAt) {
        return generateShowScheduleRegisterRequest(show, startAt, endAt, hallId);
    }

    private ShowScheduleRegisterRequest generateShowScheduleRegisterRequest(Show show,
                                                                            LocalDateTime startAt,
                                                                            LocalDateTime endAt, long hallId) {
        return new ShowScheduleRegisterRequest(
                show.getId(),
                hallId,
                startAt,
                endAt
        );
    }
}
