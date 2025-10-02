package org.mandarin.booking.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mandarin.booking.adapter.ApiStatus.BAD_REQUEST;
import static org.mandarin.booking.adapter.ApiStatus.SUCCESS;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

class ResponseWrapperTest {

    private ResponseWrapper responseWrapper;

    @BeforeEach
    void setUp() {
        responseWrapper = new ResponseWrapper();
    }

    @Test
    void shouldWrapNormalResponse() throws NoSuchMethodException {
        // Arrange
        Method method = DummyController.class.getMethod("normalResponse");
        MethodParameter methodParameter = new MethodParameter(method, -1);

        Object body = "hello world";

        // Act
        Object result = responseWrapper.beforeBodyWrite(
                body,
                methodParameter,
                MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class,
                mock(ServerHttpRequest.class),
                mock(ServerHttpResponse.class)
        );

        // Assert
        assertThat(result).isInstanceOf(SuccessResponse.class);
        SuccessResponse<?> success = (SuccessResponse<?>) result;
        assertThat(success.getData()).isEqualTo("hello world");
        assertThat(success.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void shouldNotWrapErrorResponse() throws NoSuchMethodException {
        // Arrange
        Method method = DummyController.class.getMethod("errorResponse");
        MethodParameter methodParameter = new MethodParameter(method, -1);

        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST, "something wrong");

        // Act
        Object result = responseWrapper.beforeBodyWrite(
                errorResponse,
                methodParameter,
                MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class,
                mock(ServerHttpRequest.class),
                mock(ServerHttpResponse.class)
        );

        // Assert
        assertThat(result).isInstanceOf(ErrorResponse.class);
        assertThat(((ErrorResponse) result).getData()).isEqualTo("something wrong");
    }

    static class DummyController {
        public String normalResponse() {
            return "hello";
        }

        public ErrorResponse errorResponse() {
            return new ErrorResponse(BAD_REQUEST, "something wrong");
        }
    }
}
