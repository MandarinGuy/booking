package org.mandarin.booking.adapter.webapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.webapi.ApiStatus.NOT_FOUND;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.IntegrationTest;
import org.mandarin.booking.IntegrationTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class GlobalExceptionHandlerTest {

    @Test
    void endpointNotFound(@Autowired IntegrationTestUtils testUtils){
        // Act
        var request = testUtils.get("/not-found")
                .assertFailure();

        // Assert
        assertThat(request.getStatus()).isEqualTo(NOT_FOUND);
    }
}
