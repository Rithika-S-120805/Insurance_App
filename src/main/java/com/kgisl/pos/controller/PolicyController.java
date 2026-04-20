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
        return service.savePolicy(policy);
    }

    // READ ALL
    @GetMapping
    public List<Policy> getAllPolicies() {
        return service.getAllPolicies();
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