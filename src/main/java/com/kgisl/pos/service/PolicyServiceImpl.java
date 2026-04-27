package com.kgisl.pos.service;

import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.PolicyRepository;
import com.kgisl.pos.repository.UserRepository;
// import com.kgisl.pos.service.PolicyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyRepository repository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Policy savePolicy(Policy policy) {
        // Handle user_id from transient field (from JSON input)
        if (policy.getUser_id() != null && policy.getUser_id() > 0) {
            User user = userRepository.findById(policy.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + policy.getUser_id()));
            policy.setUser(user);
        }
        // If policy has a user object (from nested JSON), verify it exists
        else if (policy.getUser() != null && policy.getUser().getUserId() != null) {
            User user = userRepository.findById(policy.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + policy.getUser().getUserId()));
            policy.setUser(user);
        }
        
        // Handle agent_id from transient field (from JSON input)
        if (policy.getAgent_id() != null && policy.getAgent_id() > 0) {
            User agent = userRepository.findById(policy.getAgent_id())
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + policy.getAgent_id()));
            policy.setAgent(agent);
        }
        // If policy has an agent object (from nested JSON), verify it exists
        else if (policy.getAgent() != null && policy.getAgent().getUserId() != null) {
            User agent = userRepository.findById(policy.getAgent().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + policy.getAgent().getUserId()));
            policy.setAgent(agent);
        }
        
        return repository.save(policy);
    }

    @Override
    public List<Policy> getAllPolicies() {
        return repository.findAllWithRelationships();
    }

    @Override
    public Optional<Policy> getPolicyById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Policy updatePolicy(Long id, Policy policyDetails) {
        Policy policy = repository.findById(id).orElseThrow();

        policy.setPolicyNumber(policyDetails.getPolicyNumber());
        
        // Handle user_id from transient field (from JSON input)
        if (policyDetails.getUser_id() != null && policyDetails.getUser_id() > 0) {
            User user = userRepository.findById(policyDetails.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + policyDetails.getUser_id()));
            policy.setUser(user);
        }
        // If policy has a user object (from nested JSON), verify it exists
        else if (policyDetails.getUser() != null && policyDetails.getUser().getUserId() != null) {
            User user = userRepository.findById(policyDetails.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + policyDetails.getUser().getUserId()));
            policy.setUser(user);
        }
        
        // Handle agent_id from transient field (from JSON input)
        if (policyDetails.getAgent_id() != null && policyDetails.getAgent_id() > 0) {
            User agent = userRepository.findById(policyDetails.getAgent_id())
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + policyDetails.getAgent_id()));
            policy.setAgent(agent);
        }
        // If policy has an agent object (from nested JSON), verify it exists
        else if (policyDetails.getAgent() != null && policyDetails.getAgent().getUserId() != null) {
            User agent = userRepository.findById(policyDetails.getAgent().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + policyDetails.getAgent().getUserId()));
            policy.setAgent(agent);
        }
        
        policy.setCoverageType(policyDetails.getCoverageType());
        policy.setPremiumAmount(policyDetails.getPremiumAmount());
        policy.setPolicyType(policyDetails.getPolicyType());
        policy.setStartDate(policyDetails.getStartDate());
        policy.setEndDate(policyDetails.getEndDate());
        policy.setStatus(policyDetails.getStatus());
        policy.setSumInsured(policyDetails.getSumInsured());
        policy.setTermInMonths(policyDetails.getTermInMonths());

        return repository.save(policy);
    }

    @Override
    public void deletePolicy(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Policy> getPoliciesByUserId(Long userId) {
        return repository.findByUser_UserId(userId);
    }

    @Override
    public List<Policy> getPoliciesByAgentId(Long agentId) {
        return repository.findByAgent_UserId(agentId);
    }
}