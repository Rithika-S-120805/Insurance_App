package com.kgisl.pos.service;

import com.kgisl.pos.entity.Payment;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.repository.PaymentRepository;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private ClaimRepository claimRepository;

    @Override
    public Payment savePayment(Payment payment) {
        // Handle policy_id from transient field (from JSON input)
        if (payment.getPolicy_id() != null && payment.getPolicy_id() > 0) {
            Policy policy = policyRepository.findById(payment.getPolicy_id())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + payment.getPolicy_id()));
            payment.setPolicy(policy);
        }
        // If payment has a policy object (from nested JSON), verify it exists
        else if (payment.getPolicy() != null && payment.getPolicy().getPolicyId() != null) {
            Policy policy = policyRepository.findById(payment.getPolicy().getPolicyId())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + payment.getPolicy().getPolicyId()));
            payment.setPolicy(policy);
        }

        // Handle claim_id from transient field (from JSON input)
        if (payment.getClaim_id() != null && payment.getClaim_id() > 0) {
            Claim claim = claimRepository.findById(payment.getClaim_id())
                    .orElseThrow(() -> new RuntimeException("Claim not found with id: " + payment.getClaim_id()));
            // Check if claim is soft-deleted
            if (claim.getIsDeleted() != null && claim.getIsDeleted()) {
                throw new RuntimeException("Cannot create payment for a deleted claim");
            }
            payment.setClaim(claim);
        }
        // If payment has a claim object (from nested JSON), verify it exists
        else if (payment.getClaim() != null && payment.getClaim().getClaimId() != null) {
            Claim claim = claimRepository.findById(payment.getClaim().getClaimId())
                    .orElseThrow(() -> new RuntimeException("Claim not found with id: " + payment.getClaim().getClaimId()));
            // Check if claim is soft-deleted
            if (claim.getIsDeleted() != null && claim.getIsDeleted()) {
                throw new RuntimeException("Cannot create payment for a deleted claim");
            }
            payment.setClaim(claim);
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
        Payment existing = paymentRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setPaymentReference(payment.getPaymentReference());

            // Handle policy_id from transient field (from JSON input)
            if (payment.getPolicy_id() != null && payment.getPolicy_id() > 0) {
                Policy policy = policyRepository.findById(payment.getPolicy_id())
                        .orElseThrow(() -> new RuntimeException("Policy not found with id: " + payment.getPolicy_id()));
                existing.setPolicy(policy);
            }
            // If payment has a policy object (from nested JSON), verify it exists
            else if (payment.getPolicy() != null && payment.getPolicy().getPolicyId() != null) {
                Policy policy = policyRepository.findById(payment.getPolicy().getPolicyId())
                        .orElseThrow(() -> new RuntimeException("Policy not found with id: " + payment.getPolicy().getPolicyId()));
                existing.setPolicy(policy);
            }

            // Handle claim_id from transient field (from JSON input)
            if (payment.getClaim_id() != null && payment.getClaim_id() > 0) {
                Claim claim = claimRepository.findById(payment.getClaim_id())
                        .orElseThrow(() -> new RuntimeException("Claim not found with id: " + payment.getClaim_id()));
                // Check if claim is soft-deleted
                if (claim.getIsDeleted() != null && claim.getIsDeleted()) {
                    throw new RuntimeException("Cannot create payment for a deleted claim");
                }
                existing.setClaim(claim);
            }
            // If payment has a claim object (from nested JSON), verify it exists
            else if (payment.getClaim() != null && payment.getClaim().getClaimId() != null) {
                Claim claim = claimRepository.findById(payment.getClaim().getClaimId())
                        .orElseThrow(() -> new RuntimeException("Claim not found with id: " + payment.getClaim().getClaimId()));
                // Check if claim is soft-deleted
                if (claim.getIsDeleted() != null && claim.getIsDeleted()) {
                    throw new RuntimeException("Cannot create payment for a deleted claim");
                }
                existing.setClaim(claim);
            }

            existing.setPaymentType(payment.getPaymentType());
            existing.setAmount(payment.getAmount());
            existing.setPaymentMethod(payment.getPaymentMethod());
            existing.setPaymentStatus(payment.getPaymentStatus());
            existing.setPaymentDate(payment.getPaymentDate());
            existing.setTransactionDate(payment.getTransactionDate());

            return paymentRepository.save(existing);
        }

        return null;
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