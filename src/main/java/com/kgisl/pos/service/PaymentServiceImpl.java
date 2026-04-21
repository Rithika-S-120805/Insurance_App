package com.kgisl.pos.service;

import com.kgisl.pos.entity.Payment;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.entity.Customer;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.PaymentRepository;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.repository.ClaimRepository;
import com.kgisl.pos.repository.CustomerRepository;
import com.kgisl.pos.repository.UserRepository;
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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

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
            payment.setClaim(claim);
        }
        // If payment has a claim object (from nested JSON), verify it exists
        else if (payment.getClaim() != null && payment.getClaim().getClaimId() != null) {
            Claim claim = claimRepository.findById(payment.getClaim().getClaimId())
                    .orElseThrow(() -> new RuntimeException("Claim not found with id: " + payment.getClaim().getClaimId()));
            payment.setClaim(claim);
        }
        
        // Handle customer_id from transient field (from JSON input)
        if (payment.getCustomer_id() != null && payment.getCustomer_id() > 0) {
            Customer customer = customerRepository.findById(payment.getCustomer_id())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + payment.getCustomer_id()));
            payment.setCustomer(customer);
        }
        // If payment has a customer object (from nested JSON), verify it exists
        else if (payment.getCustomer() != null && payment.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(payment.getCustomer().getId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + payment.getCustomer().getId()));
            payment.setCustomer(customer);
        }
        
        // Handle user_id from transient field (from JSON input)
        if (payment.getUser_id() != null && payment.getUser_id() > 0) {
            User user = userRepository.findById(payment.getUser_id())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + payment.getUser_id()));
            payment.setUser(user);
        }
        // If payment has a user object (from nested JSON), verify it exists
        else if (payment.getUser() != null && payment.getUser().getUserId() != null) {
            User user = userRepository.findById(payment.getUser().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + payment.getUser().getUserId()));
            payment.setUser(user);
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
                existing.setClaim(claim);
            }
            // If payment has a claim object (from nested JSON), verify it exists
            else if (payment.getClaim() != null && payment.getClaim().getClaimId() != null) {
                Claim claim = claimRepository.findById(payment.getClaim().getClaimId())
                        .orElseThrow(() -> new RuntimeException("Claim not found with id: " + payment.getClaim().getClaimId()));
                existing.setClaim(claim);
            }
            
            // Handle customer_id from transient field (from JSON input)
            if (payment.getCustomer_id() != null && payment.getCustomer_id() > 0) {
                Customer customer = customerRepository.findById(payment.getCustomer_id())
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + payment.getCustomer_id()));
                existing.setCustomer(customer);
            }
            // If payment has a customer object (from nested JSON), verify it exists
            else if (payment.getCustomer() != null && payment.getCustomer().getId() != null) {
                Customer customer = customerRepository.findById(payment.getCustomer().getId())
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + payment.getCustomer().getId()));
                existing.setCustomer(customer);
            }
            
            // Handle user_id from transient field (from JSON input)
            if (payment.getUser_id() != null && payment.getUser_id() > 0) {
                User user = userRepository.findById(payment.getUser_id())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + payment.getUser_id()));
                existing.setUser(user);
            }
            // If payment has a user object (from nested JSON), verify it exists
            else if (payment.getUser() != null && payment.getUser().getUserId() != null) {
                User user = userRepository.findById(payment.getUser().getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + payment.getUser().getUserId()));
                existing.setUser(user);
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
}