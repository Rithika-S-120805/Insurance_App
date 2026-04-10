package com.kgisl.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    // Link to Policy
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    // Link to Customer (Claimant)
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer claimant;

    private LocalDate dateFiled;

    private String claimStatus;

    private Double claimAmount;

    private String description;

    private String documents; // store file path / URL

    // Constructors
    public Claim() {}

    // Getters & Setters
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }

    public Customer getClaimant() { return claimant; }
    public void setClaimant(Customer claimant) { this.claimant = claimant; }

    public LocalDate getDateFiled() { return dateFiled; }
    public void setDateFiled(LocalDate dateFiled) { this.dateFiled = dateFiled; }

    public String getClaimStatus() { return claimStatus; }
    public void setClaimStatus(String claimStatus) { this.claimStatus = claimStatus; }

    public Double getClaimAmount() { return claimAmount; }
    public void setClaimAmount(Double claimAmount) { this.claimAmount = claimAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }
}