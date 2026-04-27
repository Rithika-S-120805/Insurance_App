package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.agent")
    List<Policy> findAllWithRelationships();

    @Query("SELECT p FROM Policy p WHERE p.user.userId = :userId")
    List<Policy> findByUser_UserId(Long userId);

    @Query("SELECT p FROM Policy p WHERE p.agent.userId = :agentId")
    List<Policy> findByAgent_UserId(Long agentId);
}