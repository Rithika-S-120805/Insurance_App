package com.kgisl.pos.controller;

import com.kgisl.pos.entity.Policy;
import com.kgisl.pos.service.PolicyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService service;

    // CREATE
    @PostMapping
    public Policy createPolicy(@RequestBody Policy policy) {
        try {
            System.out.println("=== CREATE POLICY REQUEST ===");
            System.out.println("Policy: " + policy);
            System.out.println("User: " + policy.getUser());
            System.out.println("User ID: " + (policy.getUser() != null ? policy.getUser().getUserId() : "NULL"));
            Policy saved = service.savePolicy(policy);
            System.out.println("Policy saved successfully with ID: " + saved.getPolicyId());
            return saved;
        } catch (Exception e) {
            System.err.println("ERROR in createPolicy: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // READ ALL
    @GetMapping
    public List<Policy> getAllPolicies() {
        try {
            System.out.println("=== GET ALL POLICIES REQUEST ===");
            List<Policy> policies = service.getAllPolicies();
            System.out.println("Total policies fetched: " + policies.size());
            return policies;
        } catch (Exception e) {
            System.err.println("ERROR in getAllPolicies: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // READ BY ID
    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return service.getPolicyById(id).orElseThrow();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Policy updatePolicy(@PathVariable Long id,
                               @RequestBody Policy policy) {
        return service.updatePolicy(id, policy);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deletePolicy(@PathVariable Long id) {
        service.deletePolicy(id);
        return "Policy deleted successfully";
    }
}