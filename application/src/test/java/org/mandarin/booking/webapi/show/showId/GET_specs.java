package org.mandarin.booking.webapi.show.showId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatStream;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.NOT_FOUND;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;

import java.time.Duration;
import java.util.Comparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mandarin.booking.domain.show.ShowDetailResponse;
import org.mandarin.booking.domain.show.ShowDetailResponse.ShowScheduleResponse;
import org.mandarin.booking.domain.show.ShowResponse;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("GET /api/show/{showId}")
class GET_specs {
    @Test
    void 존재하는_showId를_요청하면_200과_함께_공연_상세_정보가_반환된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void 존재하지_않는_showId_요청_시_NOT_FOUND를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);
        var invalidShowId = show.getId() + 9999;
        
        // Act
        var response = testUtils.get("/api/show/" + invalidShowId)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "abc", "1.5", "@#$%", "-10"})
    void 양의_정수가_아닌_showId_요청_시_BAD_REQUEST을_반환한다(
            String invalidShowId,
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + invalidShowId)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하는_공연장_ID가_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowDetailResponse.class);

        // Assert
        var hallId = response.getData().hallId();
        var hallName = response.getData().hallName();
        var fetched = testFixture.findHallById(hallId);

        assertThat(fetched).isNotNull();
        assertThat(fetched.getHallName()).isEqualTo(hallName);
        assertThat(fetched.getId()).isEqualTo(hallId);
    }

    @Test
    void 공연_일정은_마감_이전의_일정만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowDetailResponse.class);

        // Assert
        var schedules = response.getData().schedules();
        assertThat(schedules)
                .allMatch(schedule -> schedule.getEndAt()
                        .isBefore(response.getData().performanceEndDate().atStartOfDay()));

    }

    @Test
    void 공연_일정의_런타임은_시작_시간과_종료_시간의_차이와_일치한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowDetailResponse.class);

        // Assert
        var schedules = response.getData().schedules();

        assertThat(schedules)
                .allMatch(schedule -> schedule.getRuntimeMinutes() == Duration.between(schedule.getStartAt(),
                        schedule.getEndAt()).toMinutes());
    }

    @Test
    void schedules는_endAt_ASC_순으로_정렬되어_반환된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowDetailResponse.class);

        // Assert
        var schedules = response.getData().schedules();
        assertThat(schedules).isNotEmpty();
        assertThat(schedules).isSortedAccordingTo(Comparator.comparing(ShowScheduleResponse::getEndAt));
    }

    @Test
    void 영속화된_정보가_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShow(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowDetailResponse.class);

        // Assert
        var data = response.getData();
        assertThat(data.showId()).isEqualTo(show.getId());
        assertThat(data.title()).isEqualTo(show.getTitle());
        assertThat(data.type()).isEqualTo(show.getType());
        assertThat(data.rating()).isEqualTo(show.getRating());
        assertThat(data.synopsis()).isEqualTo(show.getSynopsis());
        assertThat(data.posterUrl()).isEqualTo(show.getPosterUrl());
        assertThat(data.performanceStartDate()).isEqualTo(show.getPerformanceStartDate());
        assertThat(data.performanceEndDate()).isEqualTo(show.getPerformanceEndDate());

        var schedules = data.schedules();
        assertThat(schedules).hasSize(show.getSchedules().size());
        assertThatStream(schedules.stream())
                .allMatch(res ->
                        testFixture.isMatchingScheduleInShow(res, show)
                );
    }

    @Test
    void synopsis가_없는_경우_빈_문자열로_반환된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var show = testFixture.generateShowWithNoSynopsis(5);

        // Act
        var response = testUtils.get("/api/show/" + show.getId())
                .assertSuccess(ShowDetailResponse.class);

        // Assert
        var data = response.getData();

        assertThat(data.synopsis()).isNotNull();
        assertThat(data.synopsis()).isEmpty();
    }
}
