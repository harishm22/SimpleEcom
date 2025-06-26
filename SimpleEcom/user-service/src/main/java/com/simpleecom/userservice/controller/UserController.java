package com.simpleecom.userservice.controller;

import com.simpleecom.userservice.service.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Return user profile details
        java.util.Collection<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(new UserProfileResponse(
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    // DTO class for user profile response
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserProfileResponse {
        private String username;
        private String email;
        private java.util.Collection<String> roles;
    }
}
