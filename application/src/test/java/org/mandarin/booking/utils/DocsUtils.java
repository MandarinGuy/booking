package org.mandarin.booking.utils;

import static io.restassured.RestAssured.given;
import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;
import static java.lang.StackWalker.getInstance;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.env.Environment;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.stereotype.Component;

@Component
public record DocsUtils(Environment environment,
                        ObjectMapper objectMapper) {

    private static final ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    private static final String DISPLAY_SLASH = "／";

    private static volatile boolean started = false;

    public String execute(String method, String path, Object requestBody, Map<String, String> headers,
                          Object... pathParams)
            throws Exception {
        var baseSnippet = sanitize(method, path, false);
        var methodSpecificSnippet = sanitize(method, path, true);

        boolean disableDocs = isRestDocsDisabledForCurrentCall();
        if (!disableDocs) {
            ensureStarted();
        }
        var spec = prepareSpec(headers, disableDocs);

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            spec.contentType(ContentType.JSON);
            if (requestBody != null) {
                spec.body(objectMapper.writeValueAsString(requestBody));
            }
        }

        if (!disableDocs) {
            spec = spec.filter(docFilter(baseSnippet)).filter(docFilter(methodSpecificSnippet));
        }

        var resp = switch (method.toUpperCase()) {
            case "GET" -> spec.when().get(path, pathParams);
            case "POST" -> spec.when().post(path, pathParams);
            case "PUT" -> spec.when().put(path, pathParams);
            case "PATCH" -> spec.when().patch(path, pathParams);
            case "DELETE" -> spec.when().delete(path, pathParams);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
        return resp.then().extract().asString();
    }

    private String sanitize(String method, String path, boolean withMethodSuffix) {
        String groupTitle = getCurrentTestClass()
                .flatMap(this::getDisplayNameOfClass)
                .orElseGet(() -> (method.toUpperCase() + " " + path).replace("/", DISPLAY_SLASH).trim());

        String name = groupTitle.replace("/", DISPLAY_SLASH).replaceAll("\\s+", " ").trim();

        if (withMethodSuffix) {
            String methodTitle = getCurrentTestMethodName()
                    .flatMap(mn -> getCurrentTestClass().flatMap(cls -> getDisplayNameOfMethod(cls, mn))
                            .or(() -> Optional.of(mn)))
                    .orElse("");

            if (!methodTitle.isEmpty()) {
                name = name + " - " + methodTitle;
            }
        }
        return name;
    }

    private Optional<? extends Class<?>> getCurrentTestClass() {
        try {
            return getInstance(RETAIN_CLASS_REFERENCE)
                    .walk(frames -> frames
                            .map(StackFrame::getDeclaringClass)
                            .filter(cls -> cls.getName().startsWith("org.mandarin"))
                            .filter(cls -> cls.isAnnotationPresent(IntegrationTest.class))
                            .findFirst());
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    private Optional<String> getDisplayNameOfClass(Class<?> cls) {
        DisplayName ann = cls.getAnnotation(DisplayName.class);
        return Optional.ofNullable(ann).map(DisplayName::value);
    }

    private Optional<String> getDisplayNameOfMethod(Class<?> cls, String methodName) {
        try {
            Method m = Arrays.stream(cls.getDeclaredMethods())
                    .filter(mm -> mm.getName().equals(methodName))
                    .findFirst()
                    .orElse(null);
            if (m == null) {
                return Optional.empty();
            }
            DisplayName ann = m.getAnnotation(DisplayName.class);
            return Optional.ofNullable(ann).map(DisplayName::value);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    private Optional<String> getCurrentTestMethodName() {
        try {
            return getInstance(RETAIN_CLASS_REFERENCE)
                    .walk(frames -> frames
                            .filter(f -> f.getDeclaringClass().getName().startsWith("org.mandarin"))
                            .filter(f -> f.getDeclaringClass().isAnnotationPresent(IntegrationTest.class))
                            .findFirst()
                            .map(StackFrame::getMethodName));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    private boolean isRestDocsDisabledForCurrentCall() {
        try {
            return getInstance(RETAIN_CLASS_REFERENCE)
                    .walk(frames -> frames
                            .map(StackFrame::getDeclaringClass)
                            .filter(cls -> cls.getName().startsWith("org.mandarin"))
                            .anyMatch(cls -> cls.isAnnotationPresent(NoRestDocs.class)));
        } catch (Throwable t) {
            return false;
        }
    }

    private void ensureStarted() {
        if (!started) {
            synchronized (DocsUtils.class) {
                if (!started) {
                    restDocumentation.beforeTest(DocsUtils.class, "integration-tests");
                    started = true;
                }
            }
        }
    }

    private RequestSpecification withDocs(RequestSpecification spec) {
        return spec.filter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation));
    }

    private Filter docFilter(String snippet) {
        return document(
                snippet,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );
    }

    private RequestSpecification prepareSpec(Map<String, String> headers, boolean disableDocs) {
        Integer port = environment.getProperty("local.server.port", Integer.class);
        if (port == null) {
            String p = environment.getProperty("local.server.port");
            port = (p != null) ? Integer.parseInt(p) : 0;
        }
        var spec = given()
                .port(port)
                .accept(ContentType.JSON);
        if (!disableDocs) {
            spec = withDocs(spec);
        }
        if (headers != null) {
            headers.forEach(spec::header);
        }
        return spec;
    }
}
