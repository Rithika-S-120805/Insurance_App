package com.kgisl.pos.controller;

import com.kgisl.pos.dto.LoginRequest;
import com.kgisl.pos.entity.Customer;
import com.kgisl.pos.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private CustomerService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("\n=== LOGIN REQUEST ===");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + request.getPassword());

        if (request.getEmail() == null || request.getEmail().isEmpty() || 
            request.getPassword() == null || request.getPassword().isEmpty()) {
            System.out.println("Email or password is empty");
            return ResponseEntity.status(400).body("Email and password are required");
        }

        // Debug: Show all customers in DB
        java.util.List<Customer> allCustomers = service.getAllCustomers();
        System.out.println("Customers in DB: " + allCustomers.size());
        allCustomers.forEach(c -> System.out.println("  - Email: " + c.getEmail() + " | Stored Password: " + c.getPassword()));

        Customer existing = service.login(
                request.getEmail(),
                request.getPassword()
        );

        if (existing != null) {
            System.out.println("✅ Login successful for: " + existing.getEmail());
            return ResponseEntity.ok(existing);
        } else {
            System.out.println("❌ Login failed - Invalid credentials");
            System.out.println("Attempted email: " + request.getEmail() + " | Attempted password: " + request.getPassword());
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        java.util.List<Customer> allCustomers = service.getAllCustomers();
        System.out.println("\n=== TEST ENDPOINT ===");
        System.out.println("Total customers in DB: " + allCustomers.size());
        allCustomers.forEach(c -> System.out.println("  - " + c.getEmail() + " | Password: " + c.getPassword()));
        return ResponseEntity.ok(allCustomers);
    }
}