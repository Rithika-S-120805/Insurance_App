package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.agent")
    List<Policy> findAllWithRelationships();
}