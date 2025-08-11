package org.mandarin.booking.webapi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.mandarin.booking.BookingApplication;
import org.mandarin.booking.adapter.webapi.AuthRequest;
import org.mandarin.booking.adapter.webapi.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = BookingApplication.class
)
public class POST_specs {
    @Test
    void 올바른_요청을_보내면_200_OK_상태코드를_반환한다(
            @Autowired TestRestTemplate testRestTemplate
    ){
        // Arrange
        var request = new AuthRequest("testUser", "testPassword");

        // Act
        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                request,
                TokenHolder.class
        );
        
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
