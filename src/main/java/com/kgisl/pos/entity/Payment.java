package com.kgisl.pos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(unique = true, nullable = false)
    private String paymentReference;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;
    
    // Transient field for deserializing policy_id from JSON
    @Transient
    private Long policy_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "claim_id")
    private Claim claim;
    
    // Transient field for deserializing claim_id from JSON
    @Transient
    private Long claim_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    // Transient field for deserializing customer_id from JSON
    @Transient
    private Long customer_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Transient field for deserializing user_id from JSON
    @Transient
    private Long user_id;

    @Column(nullable = false)
    private String paymentType;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String paymentMethod;
    
    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private LocalDate paymentDate;
    
    private LocalDate transactionDate;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Getters and Setters

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }

    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
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
    
    // For serialization and deserialization of claim_id
    @JsonProperty("claim_id")
    public Long getClaim_id() {
        // Return claim_id from the Claim relationship if it exists, otherwise return the transient field
        if (claim != null && claim.getClaimId() != null) {
            return claim.getClaimId();
        }
        return claim_id;
    }
    
    @JsonProperty("claim_id")
    public void setClaim_id(Long claim_id) {
        this.claim_id = claim_id;
    }
    
    // For serialization and deserialization of customer_id
    @JsonProperty("customer_id")
    public Long getCustomer_id() {
        // Return customer_id from the Customer relationship if it exists, otherwise return the transient field
        if (customer != null && customer.getId() != null) {
            return customer.getId();
        }
        return customer_id;
    }
    
    @JsonProperty("customer_id")
    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }
    
    // For serialization and deserialization of user_id
    @JsonProperty("user_id")
    public Long getUser_id() {
        // Return user_id from the User relationship if it exists, otherwise return the transient field
        if (user != null && user.getUserId() != null) {
            return user.getUserId();
        }
        return user_id;
    }
    
    @JsonProperty("user_id")
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}