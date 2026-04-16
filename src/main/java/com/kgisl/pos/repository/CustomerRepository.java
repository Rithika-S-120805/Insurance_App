package com.kgisl.pos.repository;

import com.kgisl.pos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email) AND c.password = :password")
    Customer findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

}