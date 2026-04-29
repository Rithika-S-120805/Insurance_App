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
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_reference", unique = true, nullable = false)
    private String paymentReference;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @Transient
    private Long policy_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = true)
    private Claim claim;

    @Transient
    private Long claim_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private Long user_id;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "transaction_date")
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

    @PrePersist
    public void prePersist() {

        if (paymentReference == null || paymentReference.isEmpty()) {
            paymentReference = "PAY-" + System.currentTimeMillis();
        }

        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }

        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }

        if (paymentStatus == null || paymentStatus.isEmpty()) {
            paymentStatus = "Completed";
        }
    }

    // =========================
    // Getters and Setters
    // =========================

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    // =========================
    // JSON ID Mapping
    // =========================

    @JsonProperty("policy_id")
    public Long getPolicy_id() {
        return policy != null ? policy.getPolicyId() : policy_id;
    }

    @JsonProperty("policy_id")
    public void setPolicy_id(Long policy_id) {
        this.policy_id = policy_id;
    }

    @JsonProperty("claim_id")
    public Long getClaim_id() {
        return claim != null ? claim.getClaimId() : claim_id;
    }

    @JsonProperty("claim_id")
    public void setClaim_id(Long claim_id) {
        this.claim_id = claim_id;
    }

    @JsonProperty("user_id")
    public Long getUser_id() {
        return user != null ? user.getUserId() : user_id;
    }

    @JsonProperty("user_id")
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    @JsonProperty("policyId")
    public Long getPolicyIdJson() {
        return policy != null ? policy.getPolicyId() : null;
    }

    @JsonProperty("claimId")
    public Long getClaimIdJson() {
        return claim != null ? claim.getClaimId() : null;
    }

    @JsonProperty("userId")
    public Long getUserIdJson() {
        return user != null ? user.getUserId() : null;
    }
}