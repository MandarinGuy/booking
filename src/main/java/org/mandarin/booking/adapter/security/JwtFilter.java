package org.mandarin.booking.adapter.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mandarin.booking.app.TokenUtils;
import org.mandarin.booking.domain.member.AuthException;
import org.mandarin.booking.domain.member.MemberAuthority;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String EXCEPTION = "exception";

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(AUTHORIZATION);
        if (isTokenBlank(header)) {
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", new AuthException("토큰이 비어있습니다."));
            filterChain.doFilter(request, response);
            return;
        }

        if (isBearer(header)) {
            String token = header.substring(PREFIX.length());

            try {
                var userId = tokenUtils.getClaim(token, "userId");
                var authorities = getAuthorities(token);
                var authToken = new CustomMemberAuthenticationToken(userId, authorities);

                var authenticate = authenticationManager.authenticate(authToken);
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            } catch (AuthException e) {
                log.error("Authentication Error: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                request.setAttribute(EXCEPTION, e);
            }
        }

        if (isAnonymous()) {
            request.setAttribute(EXCEPTION, new AuthException("유효한 토큰이 없습니다."));
        }

        filterChain.doFilter(request, response);
    }

    private List<MemberAuthority> getAuthorities(String token) {
        var roles = tokenUtils.getClaims(token, "roles");
        return roles.stream()
                .map(r -> r.substring(5)) // "ROLE_" 접두사 제거
                .map(MemberAuthority::valueOf).toList();
    }

    private boolean isTokenBlank(String header) {
        return header == null || header.equals("Bearer");
    }

    private boolean isBearer(String header) {
        return header.startsWith(PREFIX);
    }

    private boolean isAnonymous() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
