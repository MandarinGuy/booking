package org.mandarin.booking;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mandarin.booking.infra.webapi.ApiStatus.SUCCESS;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mandarin.booking.infra.webapi.ErrorResponse;
import org.mandarin.booking.infra.webapi.SuccessResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


public class TestResult {
    private final String path;
    private final Object request;

    private TestRestTemplate testRestTemplate;
    private ObjectMapper objectMapper;

    public TestResult(String path, Object request) {
        this.path = path;
        this.request = request;
    }

    public <R> SuccessResponse<R> assertSuccess(Class<R> responseBodyType) {
        String body = postForBody();

        if (body == null || body.isBlank() || responseBodyType == Void.class) {
            return new SuccessResponse<>(SUCCESS, null);
        }

        JsonNode node = parseJson(body);
        assertSuccessStatus(requireNonNull(node), body);

        JavaType targetType = buildSuccessType(responseBodyType);

        return deserializeSuccess(body, targetType, "SuccessResponse<" + responseBodyType.getSimpleName() + ">");
    }

    public <R> SuccessResponse<R> assertSuccess(ParameterizedTypeReference<R> responseTypeRef) {
        String body = postForBody();

        if (body == null || body.isBlank()) {
            return new SuccessResponse<>(SUCCESS, null);
        }

        JsonNode node = parseJson(body);
        assertSuccessStatus(requireNonNull(node), body);

        JavaType targetType = buildSuccessType(responseTypeRef);

        return deserializeSuccess(body, targetType, "SuccessResponse with parameterized type");
    }

    public ErrorResponse assertFailure() {
        String body = postForBody();

        JsonNode node = parseJson(body);
        assertErrorStatus(requireNonNull(node), body);

        return deserializeError(body);
    }

    TestResult setContext(TestRestTemplate testRestTemplate, ObjectMapper objectMapper) {
        this.testRestTemplate = testRestTemplate;
        this.objectMapper = objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return this;
    }

    private String postForBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var resp = testRestTemplate.postForEntity(path, new HttpEntity<>(request, headers), String.class);
        System.out.println(resp.getStatusCode());
        System.out.println(resp.getBody());
        return resp.getBody();
    }


    private JsonNode parseJson(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            fail("[Deserialization Failure] Expected JSON with status but failed to parse.\nActual response body: "
                 + body, e);
            return null; // Unreachable, added to satisfy compiler
        }
    }

    private void assertSuccessStatus(JsonNode node, String rawBody) {
        JsonNode statusNode = node.get("status");
        String status = statusNode == null ? null : statusNode.asText();
        if (status == null) {
            fail("[Assertion Failure] Expected a success response but 'status' field is missing.\nActual response body: "
                 + rawBody);
            return;
        }
        if (!"SUCCESS".equals(status)) {
            fail("[Assertion Failure] Expected SUCCESS but was '" + status
                 + "'. Use assertFailure() for error responses.\nActual response body: " + rawBody);
        }
    }

    private void assertErrorStatus(JsonNode node, String rawBody) {
        JsonNode statusNode = node.get("status");
        String status = statusNode == null ? null : statusNode.asText();
        if (status == null) {
            fail("[Assertion Failure] Expected an error response but 'status' field is missing.\nActual response body: "
                 + rawBody);
            return;
        }
        if ("SUCCESS".equals(status)) {
            fail("[Assertion Failure] Expected an error response but got SUCCESS. Use assertSuccess() for successful responses.\nActual response body: "
                 + rawBody);
        }
    }

    private JavaType buildSuccessType(Class<?> responseBodyType) {
        return objectMapper.getTypeFactory().constructParametricType(SuccessResponse.class, responseBodyType);
    }

    private JavaType buildSuccessType(ParameterizedTypeReference<?> responseTypeRef) {
        var typeFactory = objectMapper.getTypeFactory();
        JavaType innerType = typeFactory.constructType(responseTypeRef.getType());
        return typeFactory.constructParametricType(SuccessResponse.class, innerType);
    }

    private <R> SuccessResponse<R> deserializeSuccess(String body, JavaType targetType, String expectationDesc) {
        try {
            return objectMapper.readValue(body, targetType);
        } catch (Exception e) {
            fail("[Deserialization Failure] Expected " + expectationDesc
                 + " but failed to deserialize.\nActual response body: " + body, e);
            return null; // Unreachable, added to satisfy compiler
        }
    }

    private ErrorResponse deserializeError(String body) {
        try {
            return objectMapper.readValue(body, ErrorResponse.class);
        } catch (Exception e) {
            fail("[Deserialization Failure] Expected ErrorResponse but failed to deserialize.\nActual response body: "
                 + body, e);
            return null; // Unreachable, added to satisfy compiler
        }
    }

    private String describeActualDataType(JsonNode dataNode) {
        if (dataNode.isObject()) {
            List<String> fieldNames = new ArrayList<>();
            dataNode.fieldNames().forEachRemaining(fieldNames::add);
            Collections.sort(fieldNames);
            return "an object with fields " + fieldNames;
        }
        if (dataNode.isArray()) {
            return "an array";
        }
        // NodeType을 소문자로 변환하여 "a string value", "a number value" 등으로 표현합니다.
        return "a " + dataNode.getNodeType().toString().toLowerCase() + " value";
    }
}
