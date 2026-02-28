package com.upi.service;

import com.upi.dto.LoginRequest;
import com.upi.dto.LoginResponse;
import com.upi.dto.PhoneLoginRequest;
import com.upi.dto.RegisterRequest;
import com.upi.dto.UserResponse;
import com.upi.model.User;
import com.upi.repository.UserRepository;
import com.upi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) throws Exception{
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username is already registered");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email is already registered");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName()
        );
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) throws Exception {
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new Exception("Invalid username or email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }

        if (!user.getIsActive()) {
            throw new Exception("Account is disabled");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new LoginResponse(token, user.getUsername(), user.getEmail(), user.getFullName(), user.getId(), user.getIsVerified(), user.getCreatedAt());
    }

    public LoginResponse phoneLogin(PhoneLoginRequest request) throws Exception {
        String phone = request.getPhone();
        String password = request.getPassword();
        
        // Check if user with this phone exists
        Optional<User> existingUser = userRepository.findByPhone(phone);
        
        User user;
        if (existingUser.isPresent()) {
            // User exists - verify password if provided
            user = existingUser.get();
            
            if (!user.getIsActive()) {
                throw new Exception("Account is disabled");
            }
            
            // If password is provided, verify it
            if (password != null && !password.isEmpty()) {
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    throw new Exception("Invalid password");
                }
            }
        } else {
            // User doesn't exist - create new profile
            user = new User();
            user.setPhone(phone);
            user.setUsername("user_" + phone.replaceAll("[^0-9]", "")); // username from phone
            user.setEmail(phone.replaceAll("[^0-9]", "") + "@upi.temp"); // temp email
            
            // Use provided password or auto-generate one
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            } else {
                user.setPassword(passwordEncoder.encode("temp_" + System.currentTimeMillis())); // auto-generated password
            }
            
            user.setFullName("User " + phone);
            user.setIsActive(true);
            user.setIsVerified(false);
            
            user = userRepository.save(user);
        }
        
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        return new LoginResponse(token, user.getUsername(), user.getEmail(), user.getFullName(), user.getId(), user.getIsVerified(), user.getCreatedAt());
    }

    public UserResponse getUserProfile(String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));
        return convertToResponse(user);
    }

    public UserResponse updateUserProfile(String username, RegisterRequest request) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getPhone(), user.getAddress(), user.getIsActive(), user.getIsVerified(), user.getCreatedAt());
    }
}
