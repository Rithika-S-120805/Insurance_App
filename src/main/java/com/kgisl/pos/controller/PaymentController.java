package com.kgisl.pos.controller;

import com.kgisl.pos.entity.Payment;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.UserRepository;
import com.kgisl.pos.service.PaymentService;
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
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    // CREATE - Admin or Agent can create
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        try {
            Payment saved = paymentService.savePayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create payment: " + e.getMessage()));
        }
    }

    // READ ALL - Different results based on role
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            List<Payment> payments;

            // Admin sees all payments
            if (user.getRole() == User.Role.ADMIN) {
                payments = paymentService.getAllPayments();
            }
            // Agent sees payments for their assigned policies
            else if (user.getRole() == User.Role.AGENT) {
                payments = paymentService.getPaymentsByAgentId(user.getUserId());
            }
            // Customer sees only their own payments
            else if (user.getRole() == User.Role.CUSTOMER) {
                payments = paymentService.getPaymentsByCustomerId(user.getUserId());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Invalid role"));
            }

            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch payments: " + e.getMessage()));
        }
    }

    // READ BY ID - With role-based access
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            Payment payment = paymentService.getPaymentById(id);

            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Payment not found"));
            }

            // Admin can see all payments
            if (user.getRole() == User.Role.ADMIN) {
                return ResponseEntity.ok(payment);
            }
            // Customer can see only their own payments
            else if (user.getRole() == User.Role.CUSTOMER) {
                if (payment.getPolicy() != null && payment.getPolicy().getUser() != null && 
                    payment.getPolicy().getUser().getUserId().equals(user.getUserId())) {
                    return ResponseEntity.ok(payment);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. This is not your payment."));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch payment: " + e.getMessage()));
        }
    }

    // UPDATE - Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        try {
            Payment updated = paymentService.updatePayment(id, payment);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update payment: " + e.getMessage()));
        }
    }

    // DELETE - Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok(Map.of("message", "Payment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to delete payment: " + e.getMessage()));
        }
    }
}