package org.mandarin.booking;

import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.mandarin.booking.adapter.webapi.ApiResponse;
import org.mandarin.booking.adapter.webapi.ApiStatus;
import org.mandarin.booking.adapter.webapi.ErrorResponse;
import org.mandarin.booking.adapter.webapi.SuccessResponse;

public class TestResult {
    private Executor executor;

    private final String path;
    private final Object request;
    private final Map<String, String> headers = new HashMap<>();

    public TestResult(String path, Object request) {
        this.path = path;
        this.request = request;
    }

    private ObjectMapper objectMapper;

    public <T> ApiResponse<T> assertSuccess(Class<T> responseType) {
        var response = readSuccessResponse(
                getResponse(),
                responseType
        );

        if (response == null) {
            throw new AssertionError("Expected SUCCESS response, but got: " + null);
        } else if (response.getStatus() != ApiStatus.SUCCESS) {
            throw new AssertionError("Expected SUCCESS response, but got Error response: " + response);
        }

        return response;
    }

    public <T> ApiResponse<T> assertSuccess(TypeReference<T> typeReference) {
        var response = readSuccessResponse(
                getResponse(),
                typeReference
        );
        if (response == null) {
            throw new AssertionError("Expected SUCCESS response, but got: " + null);
        } else if (response.getStatus() != ApiStatus.SUCCESS) {
            throw new AssertionError("Expected SUCCESS response, but got Error response: " + response);
        }

        return response;
    }

    public ErrorResponse assertFailure() {
        var response = readErrorResponse();
        if (response == null) {
            throw new AssertionError("Expected Error response, but got: " + null);
        }else if (response.getStatus() == ApiStatus.SUCCESS) {
            throw new AssertionError("Expected Error response, but got SUCCESS: " + response);
        }
        return response;
    }

    public TestResult withHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    public TestResult withAuthorization(String token) {
        this.withHeader("Authorization", token);
        return this;
    }

    TestResult setContext(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    TestResult setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    private boolean isErrorEnvelope(String raw) {
        try {
            return objectMapper.readTree(raw).has("message");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSuccessEnvelope(String raw) {
        try {
            return objectMapper.readTree(raw).has("data");
        } catch (Exception e) {
            return false;
        }
    }

    private <T> ApiResponse<T> readSuccessResponse(String raw, Class<T> dataType) {
        try {
            if (isErrorEnvelope(raw)) {
                fail("Expected SuccessResponse but got ErrorResponse: " + raw);
            }
            var wrapperType = objectMapper.getTypeFactory()
                    .constructParametricType(SuccessResponse.class, dataType);
            return objectMapper.readValue(raw, wrapperType);
        } catch (JsonProcessingException primary) {
            try {
                if (dataType == String.class) {
                    @SuppressWarnings("unchecked")
                    T data = (T) raw;
                    return new SuccessResponse<>(ApiStatus.SUCCESS, data);
                }
                if (dataType == Void.class) {
                    return new SuccessResponse<>(ApiStatus.SUCCESS, null);
                }
                T data = objectMapper.readValue(raw, dataType);
                return new SuccessResponse<>(ApiStatus.SUCCESS, data);
            } catch (Exception fallback) {
                fail("Failed to parse SuccessResponse with data type " + dataType.getName() + ": " + primary.getMessage(), primary);
                return null;
            }
        }
    }

    private ErrorResponse readErrorResponse() {
        var response = getResponse();
        try {
            if (isSuccessEnvelope(response)) {
                fail("Expected ErrorResponse but got SuccessResponse: " + response);
            }
            return objectMapper.readValue(response, ErrorResponse.class);
        } catch (Exception e) {
            fail("Failed to parse ErrorResponse: " + e.getMessage(), e);
            return null;
        }
    }

    private <T> SuccessResponse<T> readSuccessResponse(String raw, TypeReference<T> typeRef) {
        try {
            var inner = objectMapper.getTypeFactory().constructType(typeRef);
            var wrapper = objectMapper.getTypeFactory().constructParametricType(SuccessResponse.class, inner);
            return objectMapper.readValue(raw, wrapper);
        } catch (JsonProcessingException primary) {
            try {
                if ("java.lang.String".equals(typeRef.getType().getTypeName())) {
                    @SuppressWarnings("unchecked")
                    T data = (T) raw;
                    return new SuccessResponse<>(ApiStatus.SUCCESS, data);
                }
                T data = objectMapper.readValue(raw, typeRef);
                return new SuccessResponse<>(ApiStatus.SUCCESS, data);
            } catch (Exception fallback) {
                fail("Failed to parse SuccessResponse with data type "
                     + typeRef.getType() + ": " + primary.getMessage(), primary);
                return null;
            }
        }
    }

    private String getResponse() {
        if (executor != null) {
            try {
                return executor.execute(path, request, headers);
            } catch (Exception e) {
                throw new AssertionError("Request execution failed: " + e.getMessage(), e);
            }
        }
        throw new AssertionError(
                "No HTTP executor configured for TestResult. Ensure IntegrationTestUtils sets an executor.");
    }

    @FunctionalInterface
    public interface Executor {
        String execute(String path, Object request, Map<String, String> headers) throws Exception;
    }
}
