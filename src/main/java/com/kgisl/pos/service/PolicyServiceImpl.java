package com.kgisl.pos.service;

import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.repository.PolicyRepository;
// import com.kgisl.pos.service.PolicyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyRepository repository;

    @Override
    public Policy savePolicy(Policy policy) {
        return repository.save(policy);
    }

    @Override
    public List<Policy> getAllPolicies() {
        return repository.findAll();
    }

    @Override
    public Optional<Policy> getPolicyById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Policy updatePolicy(Long id, Policy policyDetails) {
        Policy policy = repository.findById(id).orElseThrow();

        policy.setPolicyNumber(policyDetails.getPolicyNumber());
        policy.setPolicyHolder(policyDetails.getPolicyHolder());
        policy.setCoverageType(policyDetails.getCoverageType());
        policy.setPremiumAmount(policyDetails.getPremiumAmount());
        policy.setStartDate(policyDetails.getStartDate());
        policy.setEndDate(policyDetails.getEndDate());
        policy.setStatus(policyDetails.getStatus());

        return repository.save(policy);
    }

    @Override
    public void deletePolicy(Long id) {
        repository.deleteById(id);
    }
}