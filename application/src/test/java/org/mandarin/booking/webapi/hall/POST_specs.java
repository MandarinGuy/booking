package org.mandarin.booking.webapi.hall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.MemberAuthority.USER;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.FORBIDDEN;
import static org.mandarin.booking.adapter.ApiStatus.INTERNAL_SERVER_ERROR;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.ApiStatus.UNAUTHORIZED;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mandarin.booking.domain.hall.HallRegisterRequest;
import org.mandarin.booking.domain.hall.HallRegisterResponse;
import org.mandarin.booking.domain.hall.SeatRegisterRequest;
import org.mandarin.booking.domain.hall.SectionRegisterRequest;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/hall")
class POST_specs {

    @Test
    void ADMIN_권한의_토큰과_유효_본문으로_요청하면_SUCCESS와_hallId를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest(
                "hallName",
                List.of(
                        new SectionRegisterRequest(
                                "sectionName",
                                List.of(
                                        new SeatRegisterRequest("A", "1"),
                                        new SeatRegisterRequest("B", "2")
                                )
                        ),
                        new SectionRegisterRequest(
                                "sectionName2",
                                List.of(
                                        new SeatRegisterRequest("A", "1"),
                                        new SeatRegisterRequest("B", "2")
                                )
                        )
                )
        );

        // Act
        var response = testUtils.post("/api/hall",
                        request
                )
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertSuccess(HallRegisterResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void 비ADMIN_토큰으로_요청하면_ACCESS_DENIED을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("hallName", List.of());

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(USER))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(FORBIDDEN);
    }

    @Test
    void 토큰이_무효하면_UNAUTHORIZED을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("hallName", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "B")
                ))
        ));
        var invalidToken = "";

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(invalidToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    void name이_비어있으면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "B")
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void sections_빈_배열이면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", Collections.emptyList());

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void section_name이_비어있으면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("", List.of(
                        new SeatRegisterRequest("A", "B")
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void seats_빈_배열이면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("sectionName", Collections.emptyList())
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }


    @ParameterizedTest
    @CsvSource({
            "'', '1'",
            "'A', ''",
            "' ', '1'",
            "'A', ' '"
    })
    void rowNumber_또는_seatNumber가_빈_문자인_경우_BAD_REQUEST을_반환한다(
            String rowNumber,
            String seatNumber,
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest(rowNumber, seatNumber)
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 동일_섹션_내_rowNumber와_seatNumber의_조합이_중복이면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1"),
                        new SeatRegisterRequest("A", "1")
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 섹션_이름이_중복되면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1")
                )),
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1")
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void hall을_등록하면_등록한_사용자_정보도_저장된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1")
                ))
        ));
        var member = testFixture.insertDummyMember("test@test.com", "test", List.of(ADMIN));
        var authToken = testUtils.getAuthToken(member);

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(authToken)
                .assertSuccess(HallRegisterResponse.class);

        // Assert
        var hall = testFixture.findHallById(response.getData().hallId());
        assertThat(hall.getRegistantId()).isEqualTo(member.getUserId());
    }

    @Test
    void hall_name이_중복되면_INTERNAL_SERVER_ERROR을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var hallName = "name";
        var prevRequest = new HallRegisterRequest(hallName, List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1")
                ))
        ));
        var authToken = testUtils.getAuthToken(ADMIN);
        testUtils.post("/api/hall", prevRequest)
                .withAuthorization(authToken)
                .assertSuccess(HallRegisterResponse.class);
        var request = new HallRegisterRequest(hallName, List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1")
                ))));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(authToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    void section_name이_중복되면_BAD_REQUEST을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var sectionName = "sectionName";
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest(sectionName, List.of(
                        new SeatRegisterRequest("A", "1")
                )),
                new SectionRegisterRequest(sectionName, List.of(
                        new SeatRegisterRequest("A", "1")
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getData()).contains("Duplicate section names are not allowed");
    }

    @Test
    void 동일한_section_내에_중복된_죄석을_요청하면_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of(
                new SectionRegisterRequest("sectionName", List.of(
                        new SeatRegisterRequest("A", "1"),
                        new SeatRegisterRequest("A", "1")
                ))
        ));

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.hall.POST_specs#blankNameRequests")
    void hall_하위_정보가_잘못된_경우_hall도_저장되지_않는다(
            HallRegisterRequest request,// Arrange
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Act
        testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        var hallName = request.hallName();
        assertThat(testFixture.existsHallName(hallName)).isFalse();
    }

    @Test
    void sections가_비어있으면_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = new HallRegisterRequest("name", List.of());

        // Act
        var response = testUtils.post("/api/hall", request)
                .withAuthorization(testUtils.getAuthToken(ADMIN))
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getData()).contains("At least one section is required");
    }

    private static List<HallRegisterRequest> blankNameRequests() {
        return List.of(
                new HallRegisterRequest("name", List.of(
                        new SectionRegisterRequest("sectionName", List.of(
                                new SeatRegisterRequest("A", "1"),
                                new SeatRegisterRequest("A", "1")
                        ))
                )),// 동일 좌석
                new HallRegisterRequest("name", List.of(
                        new SectionRegisterRequest("sectionName", List.of(
                                new SeatRegisterRequest("A", "1"),
                                new SeatRegisterRequest("A", "2")
                        )),
                        new SectionRegisterRequest("sectionName", List.of(
                                new SeatRegisterRequest("A", "1"),
                                new SeatRegisterRequest("A", "2")
                        ))

                ))// 동일 구역 이름
        );
    }
}
