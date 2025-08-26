package org.mandarin.booking.adapter.security;

import static org.mandarin.booking.domain.member.MemberAuthority.USER;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.domain.member.AuthException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";
    private final TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (!isBearer(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIX.length());

        try {
            tokenUtils.validateToken(token);
            var userId = tokenUtils.getClaim(token, "userId");
            var authToken = new CustomMemberAuthenticationToken(userId, USER);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (AuthException e) {
            log.error("Authentication Error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isBearer(String header) {
        return header != null && header.startsWith(PREFIX);
    }
}
