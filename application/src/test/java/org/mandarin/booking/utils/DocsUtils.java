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
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.stereotype.Component;

@Component
public record DocsUtils(Environment environment,
                        ObjectMapper objectMapper) {
    private static final ManualRestDocumentation restDocumentation = new ManualRestDocumentation();


    private static volatile boolean started = false;

    public String execute(String method, String path, Object requestBody, Map<String, String> headers)
            throws Exception {
        var snippet = sanitize(method, path);
        boolean disableDocs = isRestDocsDisabledForCurrentCall();
        if (!disableDocs) {
            ensureStarted();
        }
        var spec = prepareSpec(headers, disableDocs);
        if ("POST".equals(method)) {
            spec.contentType(ContentType.JSON);
            if (requestBody != null) {
                spec.body(objectMapper.writeValueAsString(requestBody));
            }
        }
        var resp = ("GET".equals(method))
                ? (disableDocs ? spec.when().get(path)
                : spec.filter(docFilter(snippet)).when().get(path))
                : (disableDocs ? spec.when().post(path)
                        : spec.filter(docFilter(snippet)).when().post(path));
        return resp.then().extract().asString();
    }


    private String sanitize(String method, String path) {
        var name = method + path;
        name = name.replaceAll("^/+", "");
        name = name.replaceAll("[/{}]", "-");
        name = name.replaceAll("[^a-zA-Z0-9-_]", "-");
        return name.toLowerCase();
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
