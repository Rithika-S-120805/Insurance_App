package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.policy LEFT JOIN FETCH p.claim")
    List<Payment> findAllWithRelationships();

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.policy LEFT JOIN FETCH p.claim WHERE p.paymentId = :id")
    Payment findByIdWithRelationships(Long id);
}