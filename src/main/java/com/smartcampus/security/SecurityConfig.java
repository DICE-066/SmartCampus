package com.smartcampus.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationSuccessHandler successHandler) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/vendors/**", "/images/**", "/webjars/**").permitAll()

                        // ADMIN-only: full student management (matches the permission
                        // matrix — "Student Management" is an ADMIN capability, not STUDENT).
                        .requestMatchers("/students", "/students/new", "/students/edit/**",
                                "/students/save", "/students/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/students/*/delete").hasRole("ADMIN")

                        // ADMIN-only: academic record management.
                        .requestMatchers("/academic", "/academic/add", "/academic/save",
                                "/academic/records/*/edit").hasRole("ADMIN")

                        // ADMIN or STUDENT: single-record views. Path rules can't check
                        // *whose* record it is — that ownership check happens in the
                        // controller (AuthService.canAccessStudent), not here.
                        .requestMatchers("/students/me", "/students/*",
                                "/academic/me", "/academic/results").hasAnyRole("ADMIN", "STUDENT")

                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")

                        // Finance: ADMIN or FINANCE_OFFICER manage it; STUDENT only
                        // reaches their own via /finance/me -> /finance/history (ownership
                        // checked in FinanceController, same pattern as the other modules).
                        .requestMatchers("/finance", "/finance/payment-form", "/finance/record")
                                .hasAnyRole("ADMIN", "FINANCE_OFFICER")
                        .requestMatchers("/finance/me", "/finance/history")
                                .hasAnyRole("ADMIN", "FINANCE_OFFICER", "STUDENT")

                        // Documents: ADMIN or the owning STUDENT.
                        .requestMatchers("/documents", "/documents/**")
                                .hasAnyRole("ADMIN", "STUDENT")

                        // Reports: ADMIN or FINANCE_OFFICER only — not in any STUDENT capability.
                        .requestMatchers("/reports", "/reports/**")
                                .hasAnyRole("ADMIN", "FINANCE_OFFICER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .build();
    }
}
