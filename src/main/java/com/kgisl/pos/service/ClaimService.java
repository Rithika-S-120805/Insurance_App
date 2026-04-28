package com.kgisl.pos.service;

import com.kgisl.pos.entity.Claim;

import java.util.List;
import java.util.Optional;

public interface ClaimService {

    Claim saveClaim(Claim claim);

    List<Claim> getAllClaims();

    Optional<Claim> getClaimById(Long id);

    Claim updateClaim(Long id, Claim claim);

    void deleteClaim(Long id);

    void softDeleteClaim(Long id);

    List<Claim> getClaimsByAgentId(Long agentId);

    List<Claim> getClaimsByCustomerId(Long customerId);

    long getClaimsCount();
}