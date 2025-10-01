package org.mandarin.booking.webapi.hall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.MemberAuthority.ADMIN;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.domain.hall.HallRegisterRequest;
import org.mandarin.booking.domain.hall.HallRegisterResponse;
import org.mandarin.booking.domain.hall.SeatRegisterRequest;
import org.mandarin.booking.domain.hall.SectionRegisterRequest;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
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
}
