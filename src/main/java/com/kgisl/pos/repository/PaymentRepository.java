package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT DISTINCT p
        FROM Payment p
        LEFT JOIN FETCH p.policy pol
        LEFT JOIN FETCH p.claim c
        LEFT JOIN FETCH p.user u
        WHERE c IS NULL OR c.isDeleted = false
    """)
    List<Payment> findAllWithRelationships();

    @Query("""
        SELECT p
        FROM Payment p
        LEFT JOIN FETCH p.policy pol
        LEFT JOIN FETCH p.claim c
        LEFT JOIN FETCH p.user u
        WHERE p.paymentId = :id
        AND (c IS NULL OR c.isDeleted = false)
    """)
    Payment findByIdWithRelationships(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT p
        FROM Payment p
        LEFT JOIN FETCH p.policy pol
        LEFT JOIN FETCH p.claim c
        LEFT JOIN FETCH p.user u
        WHERE p.user.userId = :customerId
        AND (c IS NULL OR c.isDeleted = false)
        ORDER BY p.paymentDate DESC
    """)
    List<Payment> findByPolicy_User_UserId(@Param("customerId") Long customerId);

    @Query("""
        SELECT DISTINCT p
        FROM Payment p
        LEFT JOIN FETCH p.policy pol
        LEFT JOIN FETCH p.claim c
        LEFT JOIN FETCH p.user u
        WHERE pol.agent.userId = :agentId
        AND (c IS NULL OR c.isDeleted = false)
        ORDER BY p.paymentDate DESC
    """)
    List<Payment> findByPolicy_Agent_UserId(@Param("agentId") Long agentId);
}