package com.kgisl.pos.controller;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.entity.Payment;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.ClaimRepository;
import com.kgisl.pos.repository.PaymentRepository;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.repository.UserRepository;
import com.kgisl.pos.service.ClaimService;
import com.kgisl.pos.service.PaymentService;
import com.kgisl.pos.service.PolicyService;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private PaymentService paymentService;

    /**
     * ADMIN DASHBOARD - Get complete system analytics
     * Only ADMIN can access
     */
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // Basic stats
            long totalUsers = userRepository.count();
            long totalPolicies = policyRepository.count();
            long totalClaims = claimRepository.count();
            long totalPayments = paymentRepository.count();

            // Count by user role
            long totalAdmins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.ADMIN).count();
            long totalAgents = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.AGENT).count();
            long totalCustomers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.CUSTOMER).count();

            dashboard.put("totalUsers", totalUsers);
            dashboard.put("totalPolicies", totalPolicies);
            dashboard.put("totalClaims", totalClaims);
            dashboard.put("totalPayments", totalPayments);
            dashboard.put("stats", Map.of(
                    "admins", totalAdmins,
                    "agents", totalAgents,
                    "customers", totalCustomers
            ));

            // Get recent data
            dashboard.put("allUsers", userRepository.findAll());
            dashboard.put("allPolicies", policyRepository.findAll());
            dashboard.put("allClaims", claimRepository.findAll());
            dashboard.put("allPayments", paymentRepository.findAll());

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch admin dashboard: " + e.getMessage()));
        }
    }

    /**
     * AGENT DASHBOARD - Get agent's assigned customers and policies
     * Only AGENT can access
     */
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/agent")
    public ResponseEntity<?> getAgentDashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String agentEmail = authentication.getName();

            // Get agent user
            Optional<User> agentOptional = userRepository.findByEmail(agentEmail);
            if (agentOptional.isEmpty() || agentOptional.get().getRole() != User.Role.AGENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. Only agents can access this."));
            }

            User agent = agentOptional.get();
            Long agentId = agent.getUserId();

            Map<String, Object> dashboard = new HashMap<>();

            // Get agent's assigned policies
            List<Policy> agentPolicies = policyRepository.findByAgent_UserId(agentId);
            long totalPolicies = agentPolicies.size();

            // Get unique customers assigned to this agent
            long totalCustomers = agentPolicies.stream()
                    .map(p -> p.getUser().getUserId())
                    .distinct()
                    .count();

            // Get claims for agent's policies
            List<Claim> agentClaims = claimRepository.findByAgent_UserId(agentId);

            dashboard.put("agentInfo", agent);
            dashboard.put("totalAssignedPolicies", totalPolicies);
            dashboard.put("totalAssignedCustomers", totalCustomers);
            dashboard.put("totalClaims", agentClaims.size());
            dashboard.put("policies", agentPolicies);
            dashboard.put("claims", agentClaims);

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch agent dashboard: " + e.getMessage()));
        }
    }

    /**
     * CUSTOMER DASHBOARD - Get customer's own policies, claims, and payments
     * Only CUSTOMER can access their own data
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomerDashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerEmail = authentication.getName();

            // Get customer user
            Optional<User> customerOptional = userRepository.findByEmail(customerEmail);
            if (customerOptional.isEmpty() || customerOptional.get().getRole() != User.Role.CUSTOMER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. Only customers can access this."));
            }

            User customer = customerOptional.get();
            Long customerId = customer.getUserId();

            Map<String, Object> dashboard = new HashMap<>();

            // Get customer's policies
            List<Policy> customerPolicies = policyRepository.findByUser_UserId(customerId);

            // Get customer's claims (from their policies)
            List<Claim> customerClaims = claimRepository.findByPolicy_User_UserId(customerId);

            // Get customer's payments
            List<Payment> customerPayments = paymentRepository.findByPolicy_User_UserId(customerId);

            // Count pending payments
            long pendingPayments = customerPayments.stream()
                    .filter(p -> p.getPaymentStatus().equalsIgnoreCase("PENDING"))
                    .count();

            dashboard.put("customerInfo", customer);
            dashboard.put("totalPolicies", customerPolicies.size());
            dashboard.put("totalClaims", customerClaims.size());
            dashboard.put("totalPayments", customerPayments.size());
            dashboard.put("pendingPayments", pendingPayments);
            dashboard.put("policies", customerPolicies);
            dashboard.put("claims", customerClaims);
            dashboard.put("payments", customerPayments);

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch customer dashboard: " + e.getMessage()));
        }
    }

    /**
     * AGENT - Get list of customers assigned to agent
     * Only AGENT can access
     */
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/agent/customers")
    public ResponseEntity<?> getAgentCustomers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String agentEmail = authentication.getName();

            Optional<User> agentOptional = userRepository.findByEmail(agentEmail);
            if (agentOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Agent not found"));
            }

            User agent = agentOptional.get();
            Long agentId = agent.getUserId();

            // Get unique customers from agent's policies
            List<Policy> agentPolicies = policyRepository.findByAgent_UserId(agentId);
            List<User> customers = agentPolicies.stream()
                    .map(Policy::getUser)
                    .distinct()
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "agentId", agentId,
                    "totalCustomers", customers.size(),
                    "customers", customers
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch customers: " + e.getMessage()));
        }
    }

    /**
     * CUSTOMER - Get own policies only
     * Only CUSTOMER can access their own
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/policies")
    public ResponseEntity<?> getCustomerPolicies() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerEmail = authentication.getName();

            Optional<User> customerOptional = userRepository.findByEmail(customerEmail);
            if (customerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found"));
            }

            User customer = customerOptional.get();
            Long customerId = customer.getUserId();

            List<Policy> policies = policyRepository.findByUser_UserId(customerId);

            return ResponseEntity.ok(Map.of(
                    "customerId", customerId,
                    "totalPolicies", policies.size(),
                    "policies", policies
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch policies: " + e.getMessage()));
        }
    }

    /**
     * CUSTOMER - Get own claims only
     * Only CUSTOMER can access their own
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/claims")
    public ResponseEntity<?> getCustomerClaims() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerEmail = authentication.getName();

            Optional<User> customerOptional = userRepository.findByEmail(customerEmail);
            if (customerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found"));
            }

            User customer = customerOptional.get();
            Long customerId = customer.getUserId();

            List<Claim> claims = claimRepository.findByPolicy_User_UserId(customerId);

            return ResponseEntity.ok(Map.of(
                    "customerId", customerId,
                    "totalClaims", claims.size(),
                    "claims", claims
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch claims: " + e.getMessage()));
        }
    }

    /**
     * CUSTOMER - Get own payments only
     * Only CUSTOMER can access their own
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/payments")
    public ResponseEntity<?> getCustomerPayments() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerEmail = authentication.getName();

            Optional<User> customerOptional = userRepository.findByEmail(customerEmail);
            if (customerOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found"));
            }

            User customer = customerOptional.get();
            Long customerId = customer.getUserId();

            List<Payment> payments = paymentRepository.findByPolicy_User_UserId(customerId);

            return ResponseEntity.ok(Map.of(
                    "customerId", customerId,
                    "totalPayments", payments.size(),
                    "payments", payments
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch payments: " + e.getMessage()));
        }
    }

    /**
     * AGENT - Get own assigned policies only
     */
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/agent/policies")
    public ResponseEntity<?> getAgentPolicies() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String agentEmail = authentication.getName();

            Optional<User> agentOptional = userRepository.findByEmail(agentEmail);
            if (agentOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Agent not found"));
            }

            User agent = agentOptional.get();
            Long agentId = agent.getUserId();

            List<Policy> policies = policyRepository.findByAgent_UserId(agentId);

            return ResponseEntity.ok(Map.of(
                    "agentId", agentId,
                    "totalPolicies", policies.size(),
                    "policies", policies
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch policies: " + e.getMessage()));
        }
    }

    /**
     * AGENT - Get own assigned claims only
     */
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/agent/claims")
    public ResponseEntity<?> getAgentClaims() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String agentEmail = authentication.getName();

            Optional<User> agentOptional = userRepository.findByEmail(agentEmail);
            if (agentOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Agent not found"));
            }

            User agent = agentOptional.get();
            Long agentId = agent.getUserId();

            List<Claim> claims = claimRepository.findByAgent_UserId(agentId);

            return ResponseEntity.ok(Map.of(
                    "agentId", agentId,
                    "totalClaims", claims.size(),
                    "claims", claims
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch claims: " + e.getMessage()));
        }
    }
}
