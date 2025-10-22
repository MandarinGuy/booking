package org.mandarin.booking.webapi.show.schedule;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.MemberAuthority.DISTRIBUTOR;
import static org.mandarin.booking.MemberAuthority.USER;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.FORBIDDEN;
import static org.mandarin.booking.adapter.ApiStatus.INTERNAL_SERVER_ERROR;
import static org.mandarin.booking.adapter.ApiStatus.NOT_FOUND;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;
import static org.mandarin.booking.utils.ShowFixture.generateShowScheduleRegisterRequest;
import static org.mandarin.booking.utils.ShowFixture.getSeatUsageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest.GradeAssignmentRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterRequest.SeatUsageRequest;
import org.mandarin.booking.domain.show.ShowScheduleRegisterResponse;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/show/schedule")
public class POST_specs {

    @Test
    void 올바른_접근_토큰과_유효한_요청을_보내면_SUCCESS_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = generateShowScheduleRegisterRequest(
                show,
                sectionId,
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
    void DISTRIBUTOR_권한을_가진_사용자가_올바른_요청을_하는_경우_SUCCESS_상태코드를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = generateShowScheduleRegisterRequest(
                show,
                sectionId,
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30));

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertSuccess(ShowScheduleRegisterResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void 응답_본문에_scheduleId가_포함된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = generateShowScheduleRegisterRequest(
                show,
                sectionId,
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
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = generateShowScheduleRegisterRequest(show, sectionId);

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(USER))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(FORBIDDEN);
    }

    @Test
    void runtimeMinutes가_0_이하일_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var now = LocalDateTime.now();
        var request = generateShowScheduleRegisterRequest(show, sectionId, now, now.minusMinutes(1));

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getData()).contains("The end time must be after the start time");
    }


    @Test
    void startAt이_endAt보다_늦은_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = generateShowScheduleRegisterRequest(show,
                sectionId,
                LocalDateTime.of(2025, 9, 10, 21, 30),
                LocalDateTime.of(2025, 9, 10, 19, 0)
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
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = new ShowScheduleRegisterRequest(
                9999L,// 존재하지 않는 showId
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30),
                getSeatUsageRequest(sectionId)
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
    void 공연_기간_범위를_벗어나는_startAt_또는_endAt을_보낼_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 11));
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = new ShowScheduleRegisterRequest(
                requireNonNull(show.getId()),
                LocalDateTime.of(2023, 9, 10, 19, 0),
                LocalDateTime.of(2023, 9, 10, 21, 30),
                getSeatUsageRequest(sectionId)
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
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10)
        );
        var hallId = show.getHallId();
        var sectionId = testFixture.findSectionIdsByHallId(hallId).stream().findFirst().get();
        var request = generateShowScheduleRegisterRequest(
                show,
                sectionId,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        var nextRequest = generateShowScheduleRegisterRequest(
                show,
                sectionId,
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

    @Test
    void showId에_해당하는_hall에_해당하는_sectionId를_찾을_수_없으면_NOT_FOUND를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        var request = new ShowScheduleRegisterRequest(
                show.getId(),
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30),
                getSeatUsageRequest(9999L) // 존재하지 않는 sectionId
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
    }

    @Test
    void excludeSeatIds에_해당_section의_id가_아닌_좌석_id가_포함되면_NOT_FOUND를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        long sectionId = testFixture.findSectionIdsByHallId(show.getHallId()).stream().findFirst().get();
        var invalidSeatId = 9999L;
        var request = new ShowScheduleRegisterRequest(
                show.getId(),
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30),
                new SeatUsageRequest(
                        sectionId,
                        List.of(invalidSeatId),
                        List.of(new GradeAssignmentRequest(1L, List.of()))
                )
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void excludeSeatIds에_중복된_좌석이_있는_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.insertDummyShow(LocalDate.of(2025, 9, 10), LocalDate.of(2025, 12, 31));
        long sectionId = testFixture.findSectionIdsByHallId(show.getHallId()).stream().findFirst().get();
        var request = new ShowScheduleRegisterRequest(
                show.getId(),
                LocalDateTime.of(2025, 9, 10, 19, 0),
                LocalDateTime.of(2025, 9, 10, 21, 30),
                new SeatUsageRequest(
                        sectionId,
                        List.of(1L, 1L),
                        List.of(new GradeAssignmentRequest(1L, List.of()))
                )
        );

        // Act
        var response = testUtils.post("/api/show/schedule", request)
                .withAuthorization(testUtils.getAuthToken(DISTRIBUTOR))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }
}
