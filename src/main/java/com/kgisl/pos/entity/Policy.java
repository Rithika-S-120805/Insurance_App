package com.kgisl.pos.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(unique = true, nullable = false)
    private String policyNumber;

    // Link to User (Policy Holder)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Transient field for deserializing user_id from JSON
    @Transient
    private Long user_id;

    // Link to Agent (User)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id")
    private User agent;
    
    // Transient field for deserializing agent_id from JSON
    @Transient
    private Long agent_id;

    @Column(nullable = false)
    private String coverageType;
    
    @Column(nullable = false)
    private Double premiumAmount;
    
    @Column(name = "policy_type")
    private String policyType;

    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status;
    
    @Column(name = "sum_insured")
    private Double sumInsured;
    
    @Column(name = "term_in_months")
    private Integer termInMonths;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Constructors
    public Policy() {}

    // Getters & Setters
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public String getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPolicyType() {
        return policyType;
    }
    
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
    
    public Double getSumInsured() {
        return sumInsured;
    }
    
    public void setSumInsured(Double sumInsured) {
        this.sumInsured = sumInsured;
    }
    
    public Integer getTermInMonths() {
        return termInMonths;
    }
    
    public void setTermInMonths(Integer termInMonths) {
        this.termInMonths = termInMonths;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
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