package com.kgisl.pos.service;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.entity.Payment;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.ClaimRepository;
import com.kgisl.pos.repository.PaymentRepository;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Payment savePayment(Payment payment) {

        // ================= POLICY =================
        if (payment.getPolicy_id() != null) {
            Policy policy = policyRepository.findById(payment.getPolicy_id())
                    .orElseThrow(() -> new RuntimeException(
                            "Policy not found with ID: " + payment.getPolicy_id()));

            payment.setPolicy(policy);

            if (policy.getUser() != null) {
                payment.setUser(policy.getUser());
            }
        }

        // ================= CLAIM =================
        if (payment.getClaim_id() != null) {
            Claim claim = claimRepository.findById(payment.getClaim_id())
                    .orElseThrow(() -> new RuntimeException(
                            "Claim not found with ID: " + payment.getClaim_id()));

            if (Boolean.TRUE.equals(claim.getIsDeleted())) {
                throw new RuntimeException("Cannot create payment for deleted claim");
            }

            payment.setClaim(claim);

            // Auto-get policy from claim if policy missing
            if (payment.getPolicy() == null && claim.getPolicy() != null) {
                payment.setPolicy(claim.getPolicy());

                if (claim.getPolicy().getUser() != null) {
                    payment.setUser(claim.getPolicy().getUser());
                }
            }
        }

        // ================= USER =================
        if (payment.getUser_id() != null) {
            User user = userRepository.findById(payment.getUser_id())
                    .orElseThrow(() -> new RuntimeException(
                            "User not found with ID: " + payment.getUser_id()));

            payment.setUser(user);
        }

        // ================= AUTO GENERATED FIELDS =================
        if (payment.getPaymentReference() == null || payment.getPaymentReference().isBlank()) {
            payment.setPaymentReference(
                    "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
            );
        }

        if (payment.getTransactionDate() == null) {
            payment.setTransactionDate(LocalDate.now());
        }

        if (payment.getPaymentStatus() == null || payment.getPaymentStatus().isBlank()) {
            payment.setPaymentStatus("Completed");
        }

        if (payment.getReferenceNumber() == null || payment.getReferenceNumber().isBlank()) {
            payment.setReferenceNumber("REF-" + System.currentTimeMillis());
        }

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllWithRelationships();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findByIdWithRelationships(id);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {

        Payment existing = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // ================= POLICY =================
        if (payment.getPolicy_id() != null) {
            Policy policy = policyRepository.findById(payment.getPolicy_id())
                    .orElseThrow(() -> new RuntimeException(
                            "Policy not found with ID: " + payment.getPolicy_id()));

            existing.setPolicy(policy);

            if (policy.getUser() != null) {
                existing.setUser(policy.getUser());
            }
        }

        // ================= CLAIM =================
        if (payment.getClaim_id() != null) {
            Claim claim = claimRepository.findById(payment.getClaim_id())
                    .orElseThrow(() -> new RuntimeException(
                            "Claim not found with ID: " + payment.getClaim_id()));

            if (Boolean.TRUE.equals(claim.getIsDeleted())) {
                throw new RuntimeException("Cannot assign deleted claim");
            }

            existing.setClaim(claim);
        }

        // ================= USER =================
        if (payment.getUser_id() != null) {
            User user = userRepository.findById(payment.getUser_id())
                    .orElseThrow(() -> new RuntimeException(
                            "User not found with ID: " + payment.getUser_id()));

            existing.setUser(user);
        }

        // ================= NORMAL FIELDS =================
        existing.setPaymentType(payment.getPaymentType());
        existing.setAmount(payment.getAmount());
        existing.setPaymentMethod(payment.getPaymentMethod());
        existing.setPaymentStatus(payment.getPaymentStatus());
        existing.setPaymentDate(payment.getPaymentDate());
        existing.setTransactionDate(payment.getTransactionDate());
        existing.setRemarks(payment.getRemarks());

        if (payment.getPaymentReference() != null) {
            existing.setPaymentReference(payment.getPaymentReference());
        }

        if (payment.getReferenceNumber() != null) {
            existing.setReferenceNumber(payment.getReferenceNumber());
        }

        return paymentRepository.save(existing);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByPolicy_User_UserId(customerId);
    }

    @Override
    public List<Payment> getPaymentsByAgentId(Long agentId) {
        return paymentRepository.findByPolicy_Agent_UserId(agentId);
    }
}