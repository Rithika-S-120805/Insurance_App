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
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // CREATE PAYMENT
    // =========================
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            Optional<User> userOptional = userRepository.findByEmail(userEmail);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User loggedInUser = userOptional.get();

            // Automatically assign logged-in user if frontend did not send user
            if (payment.getUser() == null && payment.getUser_id() == null) {
                payment.setUser(loggedInUser);
            }

            Payment saved = paymentService.savePayment(payment);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create payment: " + e.getMessage()));
        }
    }

    // =========================
    // GET ALL PAYMENTS
    // =========================
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

            if (user.getRole() == User.Role.ADMIN) {
                payments = paymentService.getAllPayments();
            }
            else if (user.getRole() == User.Role.AGENT) {
                Long agentScopeId = user.getAgentId() != null
                        ? user.getAgentId()
                        : user.getUserId();

                payments = paymentService.getPaymentsByAgentId(agentScopeId);
            }
            else if (user.getRole() == User.Role.CUSTOMER) {
                payments = paymentService.getPaymentsByCustomerId(user.getUserId());
            }
            else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Invalid role"));
            }

            return ResponseEntity.ok(payments);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch payments: " + e.getMessage()));
        }
    }

    // =========================
    // GET PAYMENT BY ID
    // =========================
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

            if (user.getRole() == User.Role.ADMIN) {
                return ResponseEntity.ok(payment);
            }

            if (payment.getUser() != null &&
                    payment.getUser().getUserId().equals(user.getUserId())) {
                return ResponseEntity.ok(payment);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch payment: " + e.getMessage()));
        }
    }

    // =========================
    // UPDATE PAYMENT
    // =========================
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

    // =========================
    // DELETE PAYMENT
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);

            return ResponseEntity.ok(Map.of(
                    "message", "Payment deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to delete payment: " + e.getMessage()));
        }
    }
}