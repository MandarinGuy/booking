package org.mandarin.booking.webapi.movie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.webapi.ApiStatus.SUCCESS;
import static org.mandarin.booking.adapter.webapi.ApiStatus.UNAUTHORIZED;
import static org.mandarin.booking.domain.member.MemberAuthority.DISTRIBUTOR;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.mandarin.booking.domain.movie.MovieRegisterRequest;
import org.mandarin.booking.domain.movie.MovieRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@DisplayName("POST /api/movie")
public class POST_specs {

    @Test
    void 올바른_요청을_보내면_status가_SUCCESS이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(DISTRIBUTOR);

        var request = generateMovieRegisterRequest();

        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .withHeader("Authorization", authToken)
                .assertSuccess(MovieRegisterResponse.class);

        // Assert
        assertThat(response.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void Authorization_헤더에_유효한_accessToken이_없으면_status가_UNAUTHORIZED이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var request = generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", "2010-07-21", "AGE12");

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
        var authToken = testUtils.getAuthToken(DISTRIBUTOR);

        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .withHeader("Authorization", authToken)
                .assertFailure();

        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }
    
    @Test
    void runtimeMinutes은_0_미만이면_BAD_REQUEST이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(DISTRIBUTOR);

        // Act
        var response = testUtils.post(
                        "/api/movie",
                        generateMovieRegisterRequest("영화 제목", "감독 이름", -1, "SF", "2010-07-21", "AGE12")
                )
                .withHeader("Authorization", authToken)
                .assertFailure();
        
        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }
    
    @Test
    void releaseDate는_yyyy_MM_dd_형태를_준수하지_않으면_BAD_REQUEST이다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(DISTRIBUTOR);
        // 잘못된 날짜 형식
        var request = generateMovieRegisterRequest(
                "영화 제목", "감독 이름", 148, "SF", "21-07-2010", "AGE12"
        );
        
        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .withHeader("Authorization", authToken)
                .assertFailure();
        
        // Assert
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    }
    
    @Test
    void 올바른_요청을_보내면_응답_본문에_movieId가_존재한다(
            @Autowired IntegrationTestUtils testUtils
    ) {
        // Arrange
        var authToken = testUtils.getAuthToken(DISTRIBUTOR);
        var request = generateMovieRegisterRequest();
        
        // Act
        var response = testUtils.post(
                        "/api/movie",
                        request
                )
                .withHeader("Authorization", authToken)
                .assertSuccess(MovieRegisterResponse.class);
        
        // Assert
        assertThat(response.getData().movieId()).isNotNull();
    }

    static List<?> nullOrBlankElementRequests(){
        return List.of(
                generateMovieRegisterRequest("", "감독 이름", 148, "SF", "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", "", 148, "SF", "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "", "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", null, "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", "2010-07-21", ""),
                generateMovieRegisterRequest(null, "감독 이름", 148, "SF", "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", null, 148, "SF", "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, null, "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", null, "SF", "2010-07-21", "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", null, "AGE12"),
                generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", "2010-07-21", null)
        );
    }


    private static MovieRegisterRequest generateMovieRegisterRequest(String title, String director, Integer runtimeMinutes,
                                                                     String genre, String releaseDate, String rating) {
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
        return generateMovieRegisterRequest("영화 제목", "감독 이름", 148, "SF", "2010-07-21", "AGE12");
    }
}
