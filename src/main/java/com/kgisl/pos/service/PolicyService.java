package com.kgisl.pos.service;

import com.kgisl.pos.entity.Policy;

import java.util.List;
import java.util.Optional;

public interface PolicyService {

    Policy savePolicy(Policy policy);

    List<Policy> getAllPolicies();

    Optional<Policy> getPolicyById(Long id);

    Policy updatePolicy(Long id, Policy policy);

    void deletePolicy(Long id);

    List<Policy> getPoliciesByUserId(Long userId);

    List<Policy> getPoliciesByAgentId(Long agentId);
}