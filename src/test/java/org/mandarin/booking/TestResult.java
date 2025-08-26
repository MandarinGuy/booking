package org.mandarin.booking;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.mandarin.booking.adapter.webapi.ApiStatus;
import org.mandarin.booking.adapter.webapi.ErrorResponse;
import org.mandarin.booking.adapter.webapi.SuccessResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class TestResult {
    private final String path;
    private final Object request;
    private final Map<String, String> headers = new HashMap<>();

    public TestResult(String path, Object request) {
        this.path = path;
        this.request = request;
    }

    private TestRestTemplate testRestTemplate;
    private ObjectMapper objectMapper;

    public <T> SuccessResponse<T> assertSuccess(Class<T> responseType) {
        var response = readSuccessResponse(
                getResponse(),
                responseType
        );

        if (response == null || response.getStatus() != ApiStatus.SUCCESS) {
            throw new AssertionError("Expected SUCCESS response, but got: " + response);
        }

        return response;
    }

    public <T> SuccessResponse<T> assertSuccess(TypeReference<T> typeReference) {
        var response = readSuccessResponse(
                getResponse(),
                typeReference
        );
        if (response == null || response.getStatus() != ApiStatus.SUCCESS) {
            throw new AssertionError("Expected SUCCESS response, but got: " + response);
        }

        return response;
    }

    public ErrorResponse assertFailure() {
        var response = readErrorResponse();
        if (response == null || response.getStatus() == ApiStatus.SUCCESS) {
            throw new AssertionError("Expected Error response, but got: " + response);
        }
        return response;
    }

    public TestResult withHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    TestResult setContext(TestRestTemplate testRestTemplate, ObjectMapper objectMapper) {
        this.testRestTemplate = testRestTemplate;
        this.objectMapper = objectMapper;
        return this;
    }

    private <T> SuccessResponse<T> readSuccessResponse(String raw, Class<T> dataType) {
        try {
            if (looksLikeJson(raw)) {
                T data = (dataType == String.class)
                        ? dataType.cast(raw)
                        : objectMapper.readValue(raw, dataType);
                return new SuccessResponse<>(ApiStatus.SUCCESS, data);
            }
            var wrapperType = objectMapper.getTypeFactory()
                    .constructParametricType(SuccessResponse.class, dataType);
            return objectMapper.readValue(raw, wrapperType);
        } catch (JsonProcessingException e) {
            fail("Failed to parse SuccessResponse with data type " + dataType.getName() + ": " + e.getMessage(), e);
            return null;
        }
    }

    private <T> SuccessResponse<T> readSuccessResponse(String raw, TypeReference<T> typeRef) {
        try {
            if (looksLikeJson(raw)) {
                if (typeRef.getType().getTypeName().equals("java.lang.String")) {
                    @SuppressWarnings("unchecked")
                    T data = (T) raw;
                    return new SuccessResponse<>(ApiStatus.SUCCESS, data);
                }
                fail("Raw response is plain text and cannot be deserialized to " + typeRef.getType());
                return null;
            }
            JavaType inner = objectMapper.getTypeFactory().constructType(typeRef);
            JavaType wrapper = objectMapper.getTypeFactory()
                    .constructParametricType(SuccessResponse.class, inner);
            return objectMapper.readValue(raw, wrapper);
        } catch (JsonProcessingException e) {
            fail("Failed to parse SuccessResponse with data type " + typeRef.getType() + ": " + e.getMessage(), e);
            return null;
        }
    }

    private ErrorResponse readErrorResponse() {
        var response = getResponse();
        try {
            return objectMapper.readValue(response, ErrorResponse.class);
        } catch (Exception e) {
            fail("Failed to parse ErrorResponse: " + e.getMessage(), e);
            return null;
        }
    }

    private String getResponse() {
        var httpHeaders = new HttpHeaders();
        for (Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }
        return (request == null)
                ? testRestTemplate.exchange(path, GET, new HttpEntity<>(httpHeaders), String.class).getBody()
                : testRestTemplate.exchange(path, POST, new HttpEntity<>(request, httpHeaders), String.class).getBody();
    }

    private static boolean looksLikeJson(String s) {
        if (s == null) {
            return true;
        }
        var t = s.trim();
        return (!t.startsWith("{") || !t.endsWith("}")) && (!t.startsWith("[") || !t.endsWith("]"))
               && (!t.startsWith("\"") || !t.endsWith("\""));
    }
}
