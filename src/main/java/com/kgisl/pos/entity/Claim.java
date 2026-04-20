package com.kgisl.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "claim_number", unique = true, nullable = false)
    private String claimNumber;

    @Column(nullable = false)
    private LocalDate dateFiled;

    @Column(nullable = false)
    private String claimStatus;

    @Column(nullable = false)
    private Double claimAmount;

    @Column(name = "approved_amount")
    private Double approvedAmount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "documents")
    private String documents; // store file path / URL
    
    @Column(name = "approval_date")
    private LocalDate approvalDate;
    
    @Column(name = "resolution_date")
    private LocalDate resolutionDate;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

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
    
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public Double getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(Double approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    
    public LocalDate getResolutionDate() { return resolutionDate; }
    public void setResolutionDate(LocalDate resolutionDate) { this.resolutionDate = resolutionDate; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}