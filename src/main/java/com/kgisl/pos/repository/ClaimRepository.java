package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("SELECT c FROM Claim c LEFT JOIN FETCH c.policy LEFT JOIN FETCH c.agent")
    List<Claim> findAllWithRelationships();

    @Query("SELECT c FROM Claim c WHERE c.agent.userId = :agentId")
    List<Claim> findByAgent_UserId(Long agentId);

    @Query("SELECT c FROM Claim c WHERE c.policy.user.userId = :customerId")
    List<Claim> findByPolicy_User_UserId(Long customerId);
}