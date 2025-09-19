package org.mandarin.booking.webapi.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.ApiStatus.UNAUTHORIZED;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.adapter.SliceView;
import org.mandarin.booking.domain.show.Show;
import org.mandarin.booking.domain.show.ShowResponse;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.TestFixture;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("GET /api/show")
@IntegrationTest
public class GET_specs {

    @Test
    void Authorization_헤더가_없더라도_접근하더라도_401_Unauthorized가_발생하지_않는다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

        // Act
        var response = testUtils.get("/api/show")
                .assertSuccess(Void.class);

        // Assert
        assertThat(response.getStatus()).isNotEqualTo(UNAUTHORIZED);
    }

    @Test
    void 잘못된_토큰이나_만료_토큰을_전달해도_정상_응답을_반환한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var wrongToken = "wrong_token";

        // Act
        var response = testUtils.get("/api/show")
                .withAuthorization(wrongToken)
                .assertSuccess(Void.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void 기본_요청_시_첫번째_페이지의_10건이_반환된다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange

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
        assertThat(response.getData().contents()).isEmpty();
        assertThat(response.getData().hasNext()).isFalse();
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
}
