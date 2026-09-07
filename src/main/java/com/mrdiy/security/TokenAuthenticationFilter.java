package com.mrdiy.security;

import com.mrdiy.domain.AuthToken;
import com.mrdiy.repository.AuthTokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenRepository authTokenRepository;

    public TokenAuthenticationFilter(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String tokenValue = header.substring("Bearer ".length());
            authTokenRepository.findByToken(tokenValue)
                    .filter(token -> token.getExpiresAt() == null || token.getExpiresAt().isAfter(Instant.now()))
                    .map(AuthToken::getUser)
                    .ifPresent(user -> {
                        CurrentUser.set(user);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                user.getRoles().stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                                        .collect(Collectors.toList()));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            CurrentUser.clear();
        }
    }
}
