package com.mrdiy.service;

import com.mrdiy.dto.AuthDtos;
import com.mrdiy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void registerCreatesUserAndLoginReturnsBearerToken() {
        AuthDtos.RegisterRequest register = new AuthDtos.RegisterRequest();
        register.firstName = "Jane";
        register.lastName = "Doe";
        register.email = "jane@example.com";
        register.password = "secret123";

        authService.register(register);

        AuthDtos.LoginRequest login = new AuthDtos.LoginRequest();
        login.email = "jane@example.com";
        login.password = "secret123";
        AuthDtos.LoginResponse response = authService.login(login, false);

        assertThat(response.token).isNotBlank();
        assertThat(userRepository.findByEmail("jane@example.com")).isPresent();
    }
}
