package com.upi.controller;

import com.upi.dto.LoginRequest;
import com.upi.dto.LoginResponse;
import com.upi.dto.PhoneLoginRequest;
import com.upi.dto.RegisterRequest;
import com.upi.dto.UpdateProfileRequest;
import com.upi.dto.UserResponse;
import com.upi.service.AuthService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse user = authService.register(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/phone-login")
    public ResponseEntity<?> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        try {
            LoginResponse response = authService.phoneLogin(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            UserResponse user = authService.getUserProfile(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        try {
            String username = authentication.getName();
            
            // Create RegisterRequest for backward compatibility
            RegisterRequest regRequest = new RegisterRequest();
            regRequest.setFullName(request.getFullName());
            regRequest.setPhone(request.getPhone());
            regRequest.setAddress(request.getAddress());
            
            UserResponse user = authService.updateUserProfile(username, regRequest);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
