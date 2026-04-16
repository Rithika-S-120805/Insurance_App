package com.kgisl.pos.service;

import com.kgisl.pos.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Customer saveCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Customer updateCustomer(Long id, Customer customer);

    void deleteCustomer(Long id);

    Customer login(String email, String password);
}