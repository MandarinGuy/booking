package org.mandarin.booking.adapter.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (isBearer(header)) {
            String token = header.substring(PREFIX.length());

            try {
                var userId = tokenUtils.getClaim(token, "userId");
                var roles = tokenUtils.getClaims(token, "roles");
                var authorities = roles.stream()
                        .map(r -> r.substring(5)) // "ROLE_" 접두사 제거
                        .map(MemberAuthority::valueOf).toList();
                var authToken = new CustomMemberAuthenticationToken(userId, authorities);

                var authenticate = authenticationManager.authenticate(authToken);
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            } catch (AuthException e) {
                log.error("Authentication Error: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                request.setAttribute("exception", e);
            }
        }

        if(SecurityContextHolder.getContext().getAuthentication() == null){
            request.setAttribute("exception", new AuthException("유효한 토큰이 없습니다."));
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBearer(String header) {
        return header != null && header.startsWith(PREFIX);
    }
}
