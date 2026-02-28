package com.upi.dto;

import jakarta.validation.constraints.NotBlank;

public class PhoneLoginRequest {
    @NotBlank(message = "Phone number is required")
    private String phone;
    
    private String password; // Optional: for password-based authentication

    public PhoneLoginRequest() {}

    public PhoneLoginRequest(String phone) {
        this.phone = phone;
    }

    public PhoneLoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
