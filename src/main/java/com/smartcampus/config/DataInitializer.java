package com.smartcampus.config;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Seeds a default admin and student login so there's something to test with immediately. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "ADMIN", null));
            }
            if (userRepository.findByUsername("student").isEmpty()) {
                userRepository.save(new User("student", passwordEncoder.encode("student123"), "STUDENT", null));
            }
            if (userRepository.findByUsername("finance").isEmpty()) {
                userRepository.save(new User("finance", passwordEncoder.encode("finance123"), "FINANCE_OFFICER", null));
            }
        };
    }
}
