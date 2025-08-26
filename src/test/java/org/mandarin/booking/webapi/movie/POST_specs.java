package org.mandarin.booking.webapi.movie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.domain.movie.MovieRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/movie")
public class POST_specs {

    @Test
    void 올바른_요청을_보내면_status가_SUCCESS이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var member = testUtils.insertDummyMember();
        var jwtToken = "Bearer " + testUtils.getUserToken(member.getUserId(), member.getNickName());

        var request = new MovieRegisterRequest(
                "영화 제목",
                "감독 이름",
                148,
                "SF",
                LocalDate.of(2010, 7, 21),
                "AGE12",
                "타인의 꿈속에 진입해 아이디어를 주입하는 특수 임무를 수행하는 이야기.",
                "https://example.com/posters/inception.jpg",
                List.of("레오나르도 디카프리오",
                        "조셉 고든레빗",
                        "엘렌 페이지")
        );

        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .withHeader("Authorization", jwtToken)
                .assertSuccess(Void.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }
}
