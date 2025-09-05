package org.mandarin.booking;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = BookingApplication.class
)
@org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
@Import(TestConfig.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}
