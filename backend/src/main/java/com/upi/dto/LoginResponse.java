package com.upi.dto;

import java.time.LocalDateTime;

public class LoginResponse {
    private String token;
    private String username;
    private String email;
    private String fullName;
    private Long userId;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public LoginResponse(String token, String username, String email, String fullName, Long userId, Boolean isVerified, LocalDateTime createdAt) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.userId = userId;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
