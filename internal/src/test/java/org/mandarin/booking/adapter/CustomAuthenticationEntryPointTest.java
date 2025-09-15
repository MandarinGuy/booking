package org.mandarin.booking.adapter;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.InsufficientAuthenticationException;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint(objectMapper);

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("When exception attribute exists, it overrides authException message and writes UNAUTHORIZED json")
    void commence_withRequestAttribute() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        var sw = new java.io.StringWriter();
        var writer = new java.io.PrintWriter(sw, true);

        when(request.getAttribute(eq("exception"))).thenReturn(new Exception("test"));
        when(response.getWriter()).thenReturn(writer);

        var authException = new InsufficientAuthenticationException("auth failed msg");

        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("When exception attribute is null, use AuthenticationException message")
    void commence_withAuthExceptionMessage() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        var sw = new java.io.StringWriter();
        var writer = new java.io.PrintWriter(sw, true);

        when(request.getAttribute(eq("exception"))).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        var authException = new InsufficientAuthenticationException("auth failed msg");

        // Act
        entryPoint.commence(request, response, authException);
        writer.flush();

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }
}
