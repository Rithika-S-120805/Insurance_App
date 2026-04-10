package com.kgisl.pos.service.impl;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.repository.ClaimRepository;
import com.kgisl.pos.service.ClaimService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository repository;

    @Override
    public Claim saveClaim(Claim claim) {
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

        claim.setPolicy(claimDetails.getPolicy());
        claim.setClaimant(claimDetails.getClaimant());
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