package com.smartcampus.service;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Note: actual login is handled by Spring Security (CustomUserDetailsService +
 * the PasswordEncoder bean) via the form login configured in SecurityConfig.
 * This service exists for the SDD's authenticate()/hasPermission() methods and
 * for any manual lookups a controller might need.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
                .orElse(null);
    }

    public boolean hasPermission(User user, String requiredRole) {
        return user != null && user.getRole() != null && user.getRole().equalsIgnoreCase(requiredRole);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User currentUser(Authentication authentication) {
        if (authentication == null) return null;
        return getByUsername(authentication.getName());
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isFinanceOfficer(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FINANCE_OFFICER"));
    }

    /**
     * SDD-required permission check: ADMIN can access any student's records;
     * a STUDENT can only access the record linked to their own account
     * (User.linkedStudentId). Enforced here at the controller level because
     * Spring Security's path-based rules alone can't compare the requested
     * studentId against the logged-in user's own id.
     */
    public boolean canAccessStudent(Authentication authentication, Long studentId) {
        if (isAdmin(authentication)) return true;
        User user = currentUser(authentication);
        return user != null && studentId != null && studentId.equals(user.getLinkedStudentId());
    }

    /** Verifies the current password, then saves the new one (BCrypt-encoded). */
    public void changePassword(User user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
