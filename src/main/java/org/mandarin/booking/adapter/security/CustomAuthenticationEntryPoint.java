package org.mandarin.booking.adapter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.mandarin.booking.adapter.webapi.ApiStatus;
import org.mandarin.booking.adapter.webapi.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        var exception = (Exception)(request.getAttribute("exception"));
        var message = getMessage(authException, exception);
        var errorResponse = new ErrorResponse(ApiStatus.UNAUTHORIZED, message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private String getMessage(AuthenticationException authException, Exception exception) {
        if (exception != null) {
            return exception.getMessage();
        }
        return authException.getMessage();
    }
}
