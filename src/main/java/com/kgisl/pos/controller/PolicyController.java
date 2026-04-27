package com.kgisl.pos.controller;

import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.UserRepository;
import com.kgisl.pos.service.PolicyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService service;

    @Autowired
    private UserRepository userRepository;

    // CREATE - Admin or Agent can create
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    @PostMapping
    public ResponseEntity<?> createPolicy(@RequestBody Policy policy) {
        try {
            Policy saved = service.savePolicy(policy);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create policy: " + e.getMessage()));
        }
    }

    // READ ALL - Different results based on role
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAllPolicies() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            List<Policy> policies;

            // Admin sees all policies
            if (user.getRole() == User.Role.ADMIN) {
                policies = service.getAllPolicies();
            }
            // Agent sees only their assigned policies
            else if (user.getRole() == User.Role.AGENT) {
                policies = service.getPoliciesByAgentId(user.getUserId());
            }
            // Customer sees only their own policies
            else if (user.getRole() == User.Role.CUSTOMER) {
                policies = service.getPoliciesByUserId(user.getUserId());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Invalid role"));
            }

            return ResponseEntity.ok(policies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch policies: " + e.getMessage()));
        }
    }

    // READ BY ID - With role-based access
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPolicyById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            Optional<Policy> policyOptional = service.getPolicyById(id);

            if (policyOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Policy not found"));
            }

            Policy policy = policyOptional.get();

            // Admin can see all policies
            if (user.getRole() == User.Role.ADMIN) {
                return ResponseEntity.ok(policy);
            }
            // Agent can see policies they are assigned to
            else if (user.getRole() == User.Role.AGENT) {
                if (policy.getAgent() != null && policy.getAgent().getUserId().equals(user.getUserId())) {
                    return ResponseEntity.ok(policy);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This policy is not assigned to you."));
            }
            // Customer can see only their own policies
            else if (user.getRole() == User.Role.CUSTOMER) {
                if (policy.getUser() != null && policy.getUser().getUserId().equals(user.getUserId())) {
                    return ResponseEntity.ok(policy);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This is not your policy."));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch policy: " + e.getMessage()));
        }
    }

    // UPDATE - Admin or owning agent can update
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePolicy(@PathVariable Long id,
                                         @RequestBody Policy policy) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            Optional<Policy> existingPolicy = service.getPolicyById(id);

            if (existingPolicy.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Policy not found"));
            }

            // Admin can update any policy
            if (user.getRole() == User.Role.ADMIN) {
                Policy updated = service.updatePolicy(id, policy);
                return ResponseEntity.ok(updated);
            }
            // Agent can only update their assigned policies
            else if (user.getRole() == User.Role.AGENT) {
                if (existingPolicy.get().getAgent() != null && 
                    existingPolicy.get().getAgent().getUserId().equals(user.getUserId())) {
                    Policy updated = service.updatePolicy(id, policy);
                    return ResponseEntity.ok(updated);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This policy is not assigned to you."));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role for update"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update policy: " + e.getMessage()));
        }
    }

    // DELETE - Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePolicy(@PathVariable Long id) {
        try {
            service.deletePolicy(id);
            return ResponseEntity.ok(Map.of("message", "Policy deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to delete policy: " + e.getMessage()));
        }
    }
}