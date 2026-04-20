package com.kgisl.pos.service;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.entity.Customer;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.repository.ClaimRepository;
import com.kgisl.pos.repository.CustomerRepository;
import com.kgisl.pos.repository.PolicyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository repository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PolicyRepository policyRepository;

    @Override
    public Claim saveClaim(Claim claim) {
        // Load the actual Customer entity from database
        if (claim.getClaimant() != null && claim.getClaimant().getId() != null) {
            Customer customer = customerRepository.findById(claim.getClaimant().getId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + claim.getClaimant().getId()));
            claim.setClaimant(customer);
        }
        
        // Load the actual Policy entity from database
        if (claim.getPolicy() != null && claim.getPolicy().getPolicyId() != null) {
            Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + claim.getPolicy().getPolicyId()));
            claim.setPolicy(policy);
        }
        
        return repository.save(claim);
    }

    @Override
    public List<Claim> getAllClaims() {
        return repository.findAll();
    }

    @Override
    public Optional<Claim> getClaimById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Claim updateClaim(Long id, Claim claimDetails) {
        Claim claim = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));

        // Load the actual Customer entity from database
        if (claimDetails.getClaimant() != null && claimDetails.getClaimant().getId() != null) {
            Customer customer = customerRepository.findById(claimDetails.getClaimant().getId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + claimDetails.getClaimant().getId()));
            claim.setClaimant(customer);
        }
        
        // Load the actual Policy entity from database
        if (claimDetails.getPolicy() != null && claimDetails.getPolicy().getPolicyId() != null) {
            Policy policy = policyRepository.findById(claimDetails.getPolicy().getPolicyId())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + claimDetails.getPolicy().getPolicyId()));
            claim.setPolicy(policy);
        }

        claim.setDateFiled(claimDetails.getDateFiled());
        claim.setClaimStatus(claimDetails.getClaimStatus());
        claim.setClaimAmount(claimDetails.getClaimAmount());
        claim.setDescription(claimDetails.getDescription());
        claim.setDocuments(claimDetails.getDocuments());

        return repository.save(claim);
    }

    @Override
    public void deleteClaim(Long id) {
        repository.deleteById(id);
    }
}