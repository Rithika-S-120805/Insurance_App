package com.kgisl.pos.service;

import com.kgisl.pos.entity.Customer;
import com.kgisl.pos.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository repository;

    @Override
    public Customer saveCustomer(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = repository.findById(id).orElseThrow();

        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setStatus(customerDetails.getStatus());

        return repository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }
}