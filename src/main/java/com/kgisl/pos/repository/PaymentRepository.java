package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.policy LEFT JOIN FETCH p.claim WHERE p.claim IS NULL OR p.claim.isDeleted = false")
    List<Payment> findAllWithRelationships();

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.policy LEFT JOIN FETCH p.claim WHERE p.paymentId = :id AND (p.claim IS NULL OR p.claim.isDeleted = false)")
    Payment findByIdWithRelationships(Long id);

    @Query("SELECT p FROM Payment p WHERE p.policy.user.userId = :customerId AND (p.claim IS NULL OR p.claim.isDeleted = false)")
    List<Payment> findByPolicy_User_UserId(Long customerId);

    @Query("SELECT p FROM Payment p WHERE p.policy.agent.userId = :agentId AND (p.claim IS NULL OR p.claim.isDeleted = false)")
    List<Payment> findByPolicy_Agent_UserId(Long agentId);
}