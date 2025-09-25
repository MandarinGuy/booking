package org.mandarin.booking.webapi.show.showId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.NOT_FOUND;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
}
