package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("SELECT c FROM Claim c LEFT JOIN FETCH c.policy p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.agent LEFT JOIN FETCH c.agent WHERE c.isDeleted = false")
    List<Claim> findAllWithRelationships();

    @Query("SELECT c FROM Claim c LEFT JOIN FETCH c.policy p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.agent LEFT JOIN FETCH c.agent WHERE c.agent.userId = :agentId AND c.isDeleted = false")
    List<Claim> findByAgent_UserId(Long agentId);

    @Query("SELECT c FROM Claim c LEFT JOIN FETCH c.policy p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.agent LEFT JOIN FETCH c.agent WHERE c.policy.user.userId = :customerId AND c.isDeleted = false")
    List<Claim> findByPolicy_User_UserId(Long customerId);

    @Query("SELECT c FROM Claim c WHERE c.policy.policyId = :policyId AND c.isDeleted = false")
    List<Claim> findByPolicy_PolicyId(Long policyId);
}