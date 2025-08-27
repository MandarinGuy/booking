package org.mandarin.booking.webapi.movie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

        var request = generateMovieRegisterRequest();

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

    @Test
    void Authorization_헤더에_유효한_accessToken이_없으면_status가_UNAUTHORIZED이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", LocalDate.of(2010, 7, 21), "AGE12");

        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @ParameterizedTest
    @MethodSource("org.mandarin.booking.webapi.movie.POST_specs#nullOrBlankElementRequests")
    void title_director_runtimeMinutes_genre_releaseDate_rating이_비어있으면_BAD_REQUEST이다(
            MovieRegisterRequest request,
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var member = testUtils.insertDummyMember();
        var jwtToken = "Bearer " + testUtils.getUserToken(member.getUserId(), member.getNickName());


        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .withHeader("Authorization", jwtToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }

    static List<?> nullOrBlankElementRequests(){
        return List.of(
                generateMovieRegisterRequest("", "감독 이름", 148, "SF", LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", "", 148, "SF", LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "", LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", null, "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", LocalDate.of(2010, 7, 21), ""),
                generateMovieRegisterRequest(null, "감독 이름", 148, "SF", LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", null, 148, "SF", LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, null, LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", null, "SF", LocalDate.of(2010, 7, 21), "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", null, "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", LocalDate.of(2010, 7, 21), null)
        );
    }


    private static MovieRegisterRequest generateMovieRegisterRequest(String title, String director, Integer runtimeMinutes,
                                                                     String genre, LocalDate releaseDate, String rating) {
        return new MovieRegisterRequest(
                title,
                director,
                runtimeMinutes,
                genre,
                releaseDate,
                rating,
                "타인의 꿈속에 진입해 아이디어를 주입하는 특수 임무를 수행하는 이야기.",
                "https://example.com/posters/inception.jpg",
                List.of("레오나르도 디카프리오",
                        "조셉 고든레빗",
                        "엘렌 페이지")
        );
    }

    private static MovieRegisterRequest generateMovieRegisterRequest() {
        return generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", LocalDate.of(2010, 7, 21), "AGE12");
    }
}
