package org.mandarin.booking.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;

@ExtendWith(MockitoExtension.class)
class CommonHttpMessageConverterTest {
    @InjectMocks
    CommonHttpMessageConverter converter;

    @Test
    void readInternal_throwsUnsupportedOperation() {
        HttpInputMessage msg = mock(HttpInputMessage.class);
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.readInternal(ApiResponse.class, msg)
        );
    }
}
