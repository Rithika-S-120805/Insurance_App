package com.kgisl.pos.controller;

import com.kgisl.pos.entity.Claim;
import com.kgisl.pos.service.ClaimService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService service;

    // CREATE
    @PostMapping
    public Claim createClaim(@RequestBody Claim claim) {
        return service.saveClaim(claim);
    }

    // READ ALL
    @GetMapping
    public List<Claim> getAllClaims() {
        return service.getAllClaims();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public Claim getClaimById(@PathVariable Long id) {
        return service.getClaimById(id).orElseThrow();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Claim updateClaim(@PathVariable Long id,
                             @RequestBody Claim claim) {
        return service.updateClaim(id, claim);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteClaim(@PathVariable Long id) {
        service.deleteClaim(id);
        return "Claim deleted successfully";
    }
}   