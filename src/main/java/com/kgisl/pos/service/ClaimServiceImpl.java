package com.kgisl.pos.service;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.ClaimRepository;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository repository;
    
    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Claim saveClaim(Claim claim) {
        // Generate claimNumber if not provided
        if (claim.getClaimNumber() == null || claim.getClaimNumber().isEmpty()) {
            // Generate claim number like CLM-2024-XXX
            int year = java.time.LocalDate.now().getYear();
            long count = repository.count() + 1; // Simple counter, but not thread-safe
            claim.setClaimNumber(String.format("CLM-%d-%03d", year, count));
        }

        // Set defaults
        if (claim.getDateFiled() == null) {
            claim.setDateFiled(java.time.LocalDate.now());
        }
        if (claim.getClaimStatus() == null || claim.getClaimStatus().isEmpty()) {
            claim.setClaimStatus("PENDING");
        }

        // Handle policy_id from transient field (from JSON input)
        if (claim.getPolicy_id() != null && claim.getPolicy_id() > 0) {
            Policy policy = policyRepository.findById(claim.getPolicy_id())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + claim.getPolicy_id()));
            claim.setPolicy(policy);
        }
        // If claim has a policy object (from nested JSON), verify it exists
        else if (claim.getPolicy() != null && claim.getPolicy().getPolicyId() != null) {
            Policy policy = policyRepository.findById(claim.getPolicy().getPolicyId())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + claim.getPolicy().getPolicyId()));
            claim.setPolicy(policy);
        }
        else {
            throw new RuntimeException("Policy ID is required to create a claim");
        }
        
        // Handle agent_id from transient field (from JSON input)
        if (claim.getAgent_id() != null && claim.getAgent_id() > 0) {
            User agent = userRepository.findById(claim.getAgent_id())
                    .orElseThrow(() -> new RuntimeException("Agent not found with id: " + claim.getAgent_id()));
            claim.setAgent(agent);
        }
        // If claim has an agent object (from nested JSON), verify it exists
        else if (claim.getAgent() != null && claim.getAgent().getUserId() != null) {
            User agent = userRepository.findById(claim.getAgent().getUserId())
                    .orElseThrow(() -> new RuntimeException("Agent not found with id: " + claim.getAgent().getUserId()));
            claim.setAgent(agent);
        }
        
        return repository.save(claim);
    }

    @Override
    public List<Claim> getAllClaims() {
        return repository.findAllWithRelationships();
    }

    @Override
    public Optional<Claim> getClaimById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Claim updateClaim(Long id, Claim claimDetails) {
        Claim claim = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));

        // Handle policy_id from transient field (from JSON input)
        if (claimDetails.getPolicy_id() != null && claimDetails.getPolicy_id() > 0) {
            Policy policy = policyRepository.findById(claimDetails.getPolicy_id())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + claimDetails.getPolicy_id()));
            claim.setPolicy(policy);
        }
        // If claim has a policy object (from nested JSON), verify it exists
        else if (claimDetails.getPolicy() != null && claimDetails.getPolicy().getPolicyId() != null) {
            Policy policy = policyRepository.findById(claimDetails.getPolicy().getPolicyId())
                    .orElseThrow(() -> new RuntimeException("Policy not found with id: " + claimDetails.getPolicy().getPolicyId()));
            claim.setPolicy(policy);
        }
        
        // Handle agent_id from transient field (from JSON input)
        if (claimDetails.getAgent_id() != null && claimDetails.getAgent_id() > 0) {
            User agent = userRepository.findById(claimDetails.getAgent_id())
                    .orElseThrow(() -> new RuntimeException("Agent not found with id: " + claimDetails.getAgent_id()));
            claim.setAgent(agent);
        }
        // If claim has an agent object (from nested JSON), verify it exists
        else if (claimDetails.getAgent() != null && claimDetails.getAgent().getUserId() != null) {
            User agent = userRepository.findById(claimDetails.getAgent().getUserId())
                    .orElseThrow(() -> new RuntimeException("Agent not found with id: " + claimDetails.getAgent().getUserId()));
            claim.setAgent(agent);
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

    @Override
    public List<Claim> getClaimsByAgentId(Long agentId) {
        return repository.findByAgent_UserId(agentId);
    }

    @Override
    public List<Claim> getClaimsByCustomerId(Long customerId) {
        return repository.findByPolicy_User_UserId(customerId);
    }
}