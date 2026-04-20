package com.kgisl.pos.dto;

import com.kgisl.pos.entity.User;

public class LoginResponse {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String message;
    private Boolean success;

    public LoginResponse(User user, String message, Boolean success) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.role = user.getRole() != null ? user.getRole().toString() : null;
        this.message = message;
        this.success = success;
    }

    public LoginResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    // getters
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
    public Boolean getSuccess() { return success; }
}