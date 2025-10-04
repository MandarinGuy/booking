package org.mandarin.booking.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

class CommonHttpMessageConverterTest {
    CommonHttpMessageConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CommonHttpMessageConverter(new ObjectMapper());
    }

    @Test
    void readInternal_throwsUnsupportedOperation() {
        HttpInputMessage msg = new HttpInputMessage() {

            @Override
            @NullUnmarked
            public HttpHeaders getHeaders() {
                return null;
            }

            @Override
            @NullUnmarked
            public InputStream getBody() {
                return null;
            }
        };

        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.readInternal(ApiResponse.class, msg)
        );
    }
}
