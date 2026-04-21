package com.kgisl.pos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    // Transient field for deserializing policy_id from JSON
    @Transient
    private Long policy_id;

    // Link to Agent (User)
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;
    
    // Transient field for deserializing agent_id from JSON
    @Transient
    private Long agent_id;

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

    public User getAgent() { return agent; }
    public void setAgent(User agent) { this.agent = agent; }

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
    
    // For serialization and deserialization of policy_id
    @JsonProperty("policy_id")
    public Long getPolicy_id() {
        // Return policy_id from the Policy relationship if it exists, otherwise return the transient field
        if (policy != null && policy.getPolicyId() != null) {
            return policy.getPolicyId();
        }
        return policy_id;
    }
    
    @JsonProperty("policy_id")
    public void setPolicy_id(Long policy_id) {
        this.policy_id = policy_id;
    }
    
    // For serialization and deserialization of agent_id
    @JsonProperty("agent_id")
    public Long getAgent_id() {
        // Return agent_id from the User relationship if it exists, otherwise return the transient field
        if (agent != null && agent.getUserId() != null) {
            return agent.getUserId();
        }
        return agent_id;
    }
    
    @JsonProperty("agent_id")
    public void setAgent_id(Long agent_id) {
        this.agent_id = agent_id;
    }
}