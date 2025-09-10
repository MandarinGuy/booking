package org.mandarin.booking.webapi.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.INTERNAL_SERVER_ERROR;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.domain.show.ShowRegisterRequest;
import org.mandarin.booking.domain.show.ShowRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/show")
public class POST_specs {

    @Test
    void 올바른_요청을_보내면_status가_SUCCESS이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(ADMIN);
        var request = validShowRegisterRequest();

        // Act
        var response = testUtils.post(
                        "/api/show",
                        request
                )
                .withAuthorization(authToken)
                .assertSuccess(ShowRegisterResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void Authorization_헤더에_유효한_accessToken이_없으면_status가_UNAUTHORIZED이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = validShowRegisterRequest();

        // Act
        var response = testUtils.post(
                        "/api/show",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.show.POST_specs#nullOrBlankElementRequests")
    void title_type_rating_synopsis_posterUrl_performanceDates가_비어있으면_BAD_REQUEST이다(
            ShowRegisterRequest request,
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(ADMIN);

        // Act
        var response = testUtils.post(
                        "/api/show",
                        request
                )
                .withAuthorization(authToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 허용되지_않은_type이면_BAD_REQUEST이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(ADMIN);
        var request = new ShowRegisterRequest(
                "공연 제목",
                "MOVIE", // invalid type
                "AGE12",
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        // Act
        var response = testUtils.post(
                        "/api/show",
                        request
                )
                .withAuthorization(authToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 올바른_요청을_보내면_응답_본문에_showId가_존재한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(ADMIN);
        var request = validShowRegisterRequest();

        // Act
        var response = testUtils.post(
                        "/api/show",
                        request
                )
                .withAuthorization(authToken)
                .assertSuccess(ShowRegisterResponse.class);

        // Assert
        assertThat(response.getData().showId()).isNotNull();
    }

    @Test
    void 공연_시작일은_공연_종료일_이후면_INTERNAL_SERVER_ERROR이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(ADMIN);
        var request = new ShowRegisterRequest(
                "공연 제목",
                "MUSICAL",
                "AGE12",
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().minusDays(1)
        );

        // Act
        var response = testUtils.post(
                        "/api/show",
                        request
                )
                .withAuthorization(authToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getData()).isEqualTo("공연 시작 날짜는 종료 날짜 이후에 있을 수 없습니다.");

    }

    @SuppressWarnings("NonAsciiCharacters")
    @Test
    void 중복된_제목의_공연을_등록하면_INTERNAL_SERVER_ERROR가_발생한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(ADMIN);
        var request = validShowRegisterRequest();
        testUtils.post(
                        "/api/show",
                        request
                )
                .withAuthorization(authToken)
                .assertSuccess(ShowRegisterResponse.class);

        var duplicateTitleRequest = validShowRegisterRequest(request.title());

        // Act
        var response = testUtils.post(
                        "/api/show",
                        duplicateTitleRequest
                )
                .withAuthorization(authToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getData()).contains("이미 존재하는 공연 이름입니다:");
    }

    static List<?> nullOrBlankElementRequests() {
        return List.of(
                new ShowRegisterRequest("", "MUSICAL", "ALL", "공연 줄거리", "https://example.com/poster.jpg",
                        LocalDate.now(), LocalDate.now().plusDays(1)),
                new ShowRegisterRequest("공연 제목", "", "ALL", "공연 줄거리", "https://example.com/poster.jpg", LocalDate.now(),
                        LocalDate.now().plusDays(1)),
                new ShowRegisterRequest("공연 제목", "MUSICAL", "", "공연 줄거리", "https://example.com/poster.jpg",
                        LocalDate.now(), LocalDate.now().plusDays(1)),
                new ShowRegisterRequest("공연 제목", "MUSICAL", "ALL", "", "https://example.com/poster.jpg",
                        LocalDate.now(), LocalDate.now().plusDays(1)),
                new ShowRegisterRequest("공연 제목", "MUSICAL", "ALL", "공연 줄거리", "", LocalDate.now(),
                        LocalDate.now().plusDays(1)),
                new ShowRegisterRequest("공연 제목", "MUSICAL", "ALL", "공연 줄거리", "https://example.com/poster.jpg", null,
                        LocalDate.now().plusDays(1)),
                new ShowRegisterRequest("공연 제목", "MUSICAL", "ALL", "공연 줄거리", "https://example.com/poster.jpg",
                        LocalDate.now(), null)
        );
    }

    private ShowRegisterRequest validShowRegisterRequest() {
        return new ShowRegisterRequest(
                UUID.randomUUID().toString().substring(0, 10),
                "MUSICAL",
                "AGE12",
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }

    private ShowRegisterRequest validShowRegisterRequest(String title) {
        return new ShowRegisterRequest(
                title,
                "MUSICAL",
                "AGE12",
                "공연 줄거리",
                "https://example.com/poster.jpg",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }
}

