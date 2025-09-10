package org.mandarin.booking;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookingApplicationTests {

    @Test
    void run() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            BookingApplication.main(new String[0]);
            mocked.verify(() -> SpringApplication.run(BookingApplication.class, new String[0]));
        }
    }

}
