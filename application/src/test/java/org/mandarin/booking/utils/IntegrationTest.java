package org.mandarin.booking.utils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.mandarin.booking.BookingApplication;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = BookingApplication.class
)
@AutoConfigureRestDocs
@Import(TestConfig.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}
