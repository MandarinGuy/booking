package org.mandarin.booking.adapter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mandarin.booking.adapter.webapi.ApiStatus;
import org.mandarin.booking.adapter.webapi.ErrorResponse;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.domain.member.AuthException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";
    private final TokenUtils tokenUtils;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (isNotBearer(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIX.length());

        try {
            tokenUtils.validateToken(token);

            var userId = tokenUtils.getClaim(token, "userId");
            var preAuthToken = new PreAuthenticatedAuthenticationToken(userId, null, null);

            SecurityContextHolder.getContext().setAuthentication(preAuthToken);
            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            log.error("Authentication Error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            responseErrorMessage(response, e);
        }
    }

    private void responseErrorMessage(HttpServletResponse response, AuthException e) throws IOException {
        var errorResponse = new ErrorResponse(ApiStatus.UNAUTHORIZED, e.getMessage());
        String valueAsString = objectMapper.writeValueAsString(errorResponse);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getOutputStream().write(valueAsString.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isNotBearer(String header) {
        return header == null || !header.startsWith(PREFIX);
    }
}
