package com.mrdiy.service;

import com.mrdiy.common.ApiException;
import com.mrdiy.domain.AuthToken;
import com.mrdiy.domain.Role;
import com.mrdiy.domain.User;
import com.mrdiy.dto.AuthDtos;
import com.mrdiy.repository.AuthTokenRepository;
import com.mrdiy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final long tokenTtlHours;

    public AuthService(UserRepository userRepository,
                       AuthTokenRepository authTokenRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.auth.token-ttl-hours:24}") long tokenTtlHours) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenTtlHours = tokenTtlHours;
    }

    @Transactional
    public void register(AuthDtos.RegisterRequest request) {
        userRepository.findByEmail(request.email).ifPresent(user -> {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        });

        User user = new User();
        user.setFirstName(request.firstName);
        user.setLastName(request.lastName);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
    }

    @Transactional
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request, boolean adminOnly) {
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invalid login details"));
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid login details");
        }
        if (adminOnly && !user.getRoles().contains(Role.ADMIN)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid login details");
        }

        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setExpiresAt(Instant.now().plus(tokenTtlHours, ChronoUnit.HOURS));
        authTokenRepository.save(token);
        return new AuthDtos.LoginResponse(user, token.getToken());
    }

    @Transactional
    public void logout(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authTokenRepository.deleteByToken(authorizationHeader.substring("Bearer ".length()));
        }
    }
}
