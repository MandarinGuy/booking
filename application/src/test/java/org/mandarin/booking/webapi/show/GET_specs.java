package org.mandarin.booking.webapi.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.ApiStatus.UNAUTHORIZED;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
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
}
