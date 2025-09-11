package org.mandarin.booking.webapi.not_found;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.ApiStatus.NOT_FOUND;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.utils.IntegrationTest;
import org.mandarin.booking.utils.IntegrationTestUtils;
import org.mandarin.booking.utils.NoRestDocs;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@NoRestDocs
public class GET_specs {
    @Test
    void endpointNotFound(@Autowired IntegrationTestUtils testUtils) {
        // Act
        var request = testUtils.get("/not-found")
                .assertFailure();

        // Assert
        assertThat(request.getStatus()).isEqualTo(NOT_FOUND);
    }
}
