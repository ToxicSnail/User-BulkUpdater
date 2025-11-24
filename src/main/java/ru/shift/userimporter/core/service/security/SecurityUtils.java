package ru.shift.userimporter.core.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static CurrentUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return new CurrentUser(auth.getName(), roles);
    }

    public record CurrentUser(String username, Set<String> roles) {
        public boolean hasRole(String role) {
            return roles.stream().anyMatch(r -> r.equalsIgnoreCase("ROLE_" + role) || r.equalsIgnoreCase(role));
        }

        public boolean isAdmin() { return hasRole("ADMIN"); }
        public boolean isOperator() { return hasRole("OPERATOR"); }
        public boolean isAuditor() { return hasRole("AUDITOR"); }
    }
}
