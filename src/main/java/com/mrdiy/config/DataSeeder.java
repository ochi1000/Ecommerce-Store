package com.mrdiy.config;

import com.mrdiy.domain.Role;
import com.mrdiy.domain.User;
import com.mrdiy.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@mrdiy.local").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setEmail("admin@mrdiy.local");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setRoles(Collections.singleton(Role.ADMIN));
                userRepository.save(admin);
            }
        };
    }
}
