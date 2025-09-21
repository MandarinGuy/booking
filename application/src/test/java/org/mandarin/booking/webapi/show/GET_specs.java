package org.mandarin.booking.webapi.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatStream;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.Show.Rating;
import org.mandarin.booking.domain.show.Show.Type;
import org.mandarin.booking.domain.show.ShowResponse;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("GET /api/show")
@IntegrationTest
public class GET_specs {

//    @Test
//    void Authorization_헤더가_없더라도_접근하더라도_401_Unauthorized가_발생하지_않는다(
//            @Autowired IntegrationTestUtils testUtils
//    ) {
//        // Arrange
//
//        // Act
//        var response = testUtils.get("/api/show")
//                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
//                });
//
//        // Assert
//        assertThat(response.getStatus()).isNotEqualTo(UNAUTHORIZED);
//    }


    @BeforeEach
    void setUp(@Autowired TestFixture testFixture) {
        testFixture.removeShows();
    }

    @Test
    void 기본_요청_시_첫번째_페이지의_10건이_반환된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(10);

        // Act
        var response = testUtils.get("/api/show")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().contents().size()).isEqualTo(10);
    }

    @Test
    void 공연이_존재하지_않을_경우_빈_contents_hasNext는false를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().hasNext()).isFalse();
        assertThat(response.getData().contents()).isEmpty();
    }

    @Test
    void 실제로_저장된_공연_정보가_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        List<Show> showRegisterResponses = testFixture.generateShows(10);

        // Act
        var response = testUtils.get("/api/show")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        for (Show res : showRegisterResponses) {
            assertThat(response.getData().contents().stream()
                    .anyMatch(show -> show.showId().equals(res.getId())))
                    .isTrue();
        }
    }

    @Test
    void 초과_페이지_요청_시_빈_contents와_hasNext는_false를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show?page=2")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().hasNext()).isFalse();
        assertThat(response.getData().contents()).isEmpty();
    }

    @Test
    void size가_100보다_큰_요청_시_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show?size=101")
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void page가_0보다_작은_요청_시_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show?page=-1")
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void size가_1보다_작은_요청_시_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show?size=0")
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 부적절한_type으로_요청하는_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show?type=AAA")// invalid type
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 부적절한_rating으로_요청하는_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show?rating=AAA")
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 지정된_type이_존재한다면_해당_type_공연만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(10, Type.MUSICAL);

        // Act
        var response = testUtils.get("/api/show?type=MUSICAL")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThatStream(response.getData().contents().stream())
                .allMatch(show -> show.type().equals(Type.MUSICAL));
    }

    @Test
    void 지정된_rating이_존재한다면_해당_rating_공연만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(10, Rating.ALL);

        // Act
        var response = testUtils.get("/api/show?rating=ALL")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThatStream(response.getData().contents().stream())
                .allMatch(show -> show.rating().equals(Rating.ALL));

    }

    @Test
    void q값이_비어있지_않다면_제목에_q가_포함된_공연만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        var titlePart = "titlePart";
        testFixture.generateShows(10, titlePart);

        // Act
        var response = testUtils.get("/api/show?q=titlePart")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThatStream(response.getData().contents().stream().map(ShowResponse::title))
                .allMatch(title -> title.contains(titlePart));

    }

    @Test
    void 여러_건이_존재할_경우_performanceStartDate_DESC_title_ASC_순으로_정렬된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20);

        // Act
        var response = testUtils.get("/api/show?size=20")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThatStream(response.getData().contents().stream())
                .isSortedAccordingTo(Comparator.comparing(ShowResponse::performanceStartDate)
                        .thenComparing(ShowResponse::title));
    }

    @Test
    void from에서_to까지_기간과_겹치는_공연만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, 10, 10);
        var from = LocalDate.now().minusDays(1).toString();
        var to = LocalDate.now().plusDays(1).toString();
        // Act
        var response = testUtils.get("/api/show?from=" + from + "&to=" + to)
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().contents().stream())
                .allMatch(show -> !show.performanceStartDate().isAfter(LocalDate.parse(to))// 결과의 시작일은 요청 종요일보다 이후가 아니며
                                  && !show.performanceEndDate()
                        .isBefore(LocalDate.parse(from)));//결과의 종료일은 요청 시작일보다 이전이 아니다
    }

    @Test
    void from만_지정_시_해당_일자_이후_공연만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, 10, 10);

        // Act
        var response = testUtils.get("/api/show?from=" + LocalDate.now().plusDays(1))
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().contents().stream())
                .allMatch(show -> show.performanceStartDate().isAfter(LocalDate.now().plusDays(1)));
    }

    @Test
    void to만_지정_시_해당_일자_이전_공연만_조회된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, 10, 10);

        // Act
        var response = testUtils.get("/api/show?to=" + LocalDate.now().minusDays(3))
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().contents().stream().map(ShowResponse::performanceEndDate))
                .allMatch(date -> date.isBefore(LocalDate.now().minusDays(3)));
    }

    @Test
    void 기간이_서로_맞물리지_않는_경우_빈_contents를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, 10, 10);
        var from = LocalDate.now().plusDays(11).toString();
        var to = LocalDate.now().plusDays(21).toString();

        // Act
        var response = testUtils.get("/api/show?from=" + from + "&to=" + to)
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().hasNext()).isFalse();
        assertThat(response.getData().contents()).isEmpty();
    }

    @Test
    void from_또는_to_형식이_잘못된_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, 10, 10);
        var from = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        var to = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Act
        var response = testUtils.get("/api/show?from=" + from + "&to=" + to)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void from이_to이후인_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, 10, 10);
        var from = LocalDate.now().plusDays(1).toString();
        var to = LocalDate.now().minusDays(1).toString();

        // Act
        var response = testUtils.get("/api/show?from=" + from + "&to=" + to)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void q가_공백인_경우_BAD_REQUEST를_반환한다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20, "title");
        var q = " ";

        // Act
        var response = testUtils.get("/api/show?q=" + q)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 마지막_페이지에서_hasNext가_거짓으로_반환된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20);

        // Act
        var response = testUtils.get("/api/show?page=1")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().hasNext()).isFalse();
    }

    @Test
    void 마지막_페이지가_아닌_경우_hasNext가_참으로_반환된다(
            @Autowired IntegrationTestUtils testUtils,
            @Autowired TestFixture testFixture
    ) {
        // Arrange
        testFixture.generateShows(20);

        // Act
        var response = testUtils.get("/api/show?page=0")
                .assertSuccess(new TypeReference<SliceView<ShowResponse>>() {
                });

        // Assert
        assertThat(response.getData().hasNext()).isTrue();
    }
}
