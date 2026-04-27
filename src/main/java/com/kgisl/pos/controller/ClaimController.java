package com.kgisl.pos.controller;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.UserRepository;
import com.kgisl.pos.service.ClaimService;

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
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService service;

    @Autowired
    private UserRepository userRepository;

    // CREATE - Admin, Agent, or Customer can create
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> createClaim(@RequestBody Claim claim) {
        try {
            Claim saved = service.saveClaim(claim);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create claim: " + e.getMessage()));
        }
    }

    // READ ALL - Different results based on role
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAllClaims() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            List<Claim> claims;

            // Admin sees all claims
            if (user.getRole() == User.Role.ADMIN) {
                claims = service.getAllClaims();
            }
            // Agent sees only their assigned claims
            else if (user.getRole() == User.Role.AGENT) {
                claims = service.getClaimsByAgentId(user.getUserId());
            }
            // Customer sees only their own claims
            else if (user.getRole() == User.Role.CUSTOMER) {
                claims = service.getClaimsByCustomerId(user.getUserId());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Invalid role"));
            }

            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch claims: " + e.getMessage()));
        }
    }

    // READ BY ID - With role-based access
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClaimById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            Optional<Claim> claimOptional = service.getClaimById(id);

            if (claimOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Claim not found"));
            }

            Claim claim = claimOptional.get();

            // Admin can see all claims
            if (user.getRole() == User.Role.ADMIN) {
                return ResponseEntity.ok(claim);
            }
            // Agent can see claims they are assigned to
            else if (user.getRole() == User.Role.AGENT) {
                if (claim.getAgent() != null && claim.getAgent().getUserId().equals(user.getUserId())) {
                    return ResponseEntity.ok(claim);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This claim is not assigned to you."));
            }
            // Customer can see only their own claims
            else if (user.getRole() == User.Role.CUSTOMER) {
                if (claim.getPolicy() != null && claim.getPolicy().getUser() != null && 
                    claim.getPolicy().getUser().getUserId().equals(user.getUserId())) {
                    return ResponseEntity.ok(claim);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This is not your claim."));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch claim: " + e.getMessage()));
        }
    }

    // UPDATE - Admin or owning agent can update
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClaim(@PathVariable Long id,
                                        @RequestBody Claim claim) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            Optional<Claim> existingClaim = service.getClaimById(id);

            if (existingClaim.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Claim not found"));
            }

            // Admin can update any claim
            if (user.getRole() == User.Role.ADMIN) {
                Claim updated = service.updateClaim(id, claim);
                return ResponseEntity.ok(updated);
            }
            // Agent can only update their assigned claims
            else if (user.getRole() == User.Role.AGENT) {
                if (existingClaim.get().getAgent() != null && 
                    existingClaim.get().getAgent().getUserId().equals(user.getUserId())) {
                    Claim updated = service.updateClaim(id, claim);
                    return ResponseEntity.ok(updated);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This claim is not assigned to you."));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role for update"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update claim: " + e.getMessage()));
        }
    }

    // DELETE - Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClaim(@PathVariable Long id) {
        try {
            service.deleteClaim(id);
            return ResponseEntity.ok(Map.of("message", "Claim deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to delete claim: " + e.getMessage()));
        }
    }
}   