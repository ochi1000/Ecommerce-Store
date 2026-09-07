package com.mrdiy.dto;

import com.mrdiy.domain.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public final class AuthDtos {
    private AuthDtos() {
    }

    public static class RegisterRequest {
        @NotBlank
        public String firstName;
        public String lastName;
        @Email
        @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class LoginRequest {
        @Email
        @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class LoginResponse {
        public User user;
        public String token;

        public LoginResponse(User user, String token) {
            this.user = user;
            this.token = token;
        }
    }
}
