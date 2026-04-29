package com.kgisl.pos.controller;

import com.kgisl.pos.dto.LoginRequest;
import com.kgisl.pos.dto.LoginResponse;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/users", "/api/admin/users"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyRepository policyRepository;

    /**
     * Login endpoint (moved to AuthController but kept for backward compatibility)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .body(Map.of("message", "Use /api/auth/login instead"));
    }

    /**
     * Get all users - Admin only
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // Handle unauthenticated or anonymous users
            if (authentication == null ||
    !authentication.isAuthenticated() ||
    authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
                return ResponseEntity.ok(List.of()); // Return empty list for anonymous users
            }
            
            User authenticatedUser = userService.getUserByEmail(authentication.getName());
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authenticated user not found"));
            }

            List<User> users = userService.getAllUsers();

            // Keep database agentId when present; only fallback to userId for legacy/null agent mappings.
            users.forEach(user -> {
                if (user.getRole() == User.Role.AGENT && user.getAgentId() == null && user.getUserId() != null) {
                    user.setAgentId(user.getUserId());
                }
            });

            if (authenticatedUser.getRole() == User.Role.ADMIN) {
                return ResponseEntity.ok(users);
            }

            if (authenticatedUser.getRole() == User.Role.AGENT) {
                Long agentKey = authenticatedUser.getAgentId() != null ? authenticatedUser.getAgentId() : authenticatedUser.getUserId();
                List<User> assignedCustomers = policyRepository.findByAgent_UserId(agentKey).stream()
                        .map(Policy::getUser)
                        .filter(Objects::nonNull)
                        .filter(user -> user.getRole() == User.Role.CUSTOMER)
                        .filter(user -> user.getUserId() != null)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(User::getUserId, user -> user, (existing, duplicate) -> existing),
                                customerMap -> List.copyOf(customerMap.values())));
                return ResponseEntity.ok(assignedCustomers);
            }

            if (authenticatedUser.getRole() == User.Role.CUSTOMER) {
                return ResponseEntity.ok(users.stream()
                        .filter(user -> Objects.equals(user.getUserId(), authenticatedUser.getUserId()))
                        .collect(Collectors.toList()));
            }

            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving users: " + e.getMessage()));
        }
    }

    /**
     * Create new user - Admin only
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Authentication required"));
            }

                User authenticatedUser = userService.getUserByEmail(authentication.getName());

                if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Authenticated user not found"));
            }

            if (authenticatedUser.getRole() != User.Role.ADMIN && authenticatedUser.getRole() != User.Role.AGENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only ADMIN or AGENT users can create accounts"));
            }

            if (authenticatedUser.getRole() == User.Role.AGENT && user.getRole() != null && user.getRole() != User.Role.CUSTOMER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Agents can only create CUSTOMER accounts"));
            }

            if (authenticatedUser.getRole() == User.Role.AGENT) {
                Long agentKey = authenticatedUser.getAgentId() != null ? authenticatedUser.getAgentId() : authenticatedUser.getUserId();
                user.setAgentId(agentKey);
                if (user.getRole() == null) {
                    user.setRole(User.Role.CUSTOMER);
                }
            }

            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "User with this email already exists"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid user data: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating user: " + e.getMessage()));
        }
    }

    /**
     * Get user by ID - Admin only or self
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    /**
     * Update user - Admin only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    /**
     * Delete user - Admin only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting user: " + e.getMessage()));
        }
    }
}