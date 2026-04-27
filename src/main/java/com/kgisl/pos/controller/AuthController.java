package com.kgisl.pos.controller;

import com.kgisl.pos.dto.LoginRequest;
import com.kgisl.pos.dto.LoginResponse;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.security.TokenProvider;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Login endpoint - Authenticates user credentials and returns JWT token
     * 
     * @param request LoginRequest containing email and password
     * @return ResponseEntity with token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LoginResponse("Email is required", false));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LoginResponse("Password is required", false));
            }

            // Authenticate user credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get authenticated user from database (Spring Security already verified credentials)
            User user = userService.getUserByEmail(request.getEmail());

            if (user != null) {
                // Generate JWT token
                String token = tokenProvider.generateTokenFromUsername(user.getEmail(), user.getRole().toString());

                // Return token and user details
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("success", true);
                response.put("token", token);
                response.put("user", user);
                response.put("role", user.getRole().toString());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("Invalid email or password", false));
            }

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Invalid email or password", false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("Server error", false));
        }
    }

    /**
     * Test endpoint - Returns all users in the database
     * 
     * @return ResponseEntity with list of all users
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        List<User> allUsers = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("total", allUsers.size());
        response.put("users", allUsers);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID endpoint - Requires authentication
     * 
     * @param id User ID
     * @return ResponseEntity with user details
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoginResponse("User not found with ID: " + id, false));
        }
    }

    /**
     * Register new user endpoint
     * 
     * @param user User object with registration details
     * @return ResponseEntity with created user and token
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Email is required", false));
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Username is required", false));
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Password is required", false));
        }

        try {
            User createdUser = userService.createUser(user);
            
            // Generate token for newly created user
            String token = tokenProvider.generateTokenFromUsername(createdUser.getEmail(), createdUser.getRole().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("success", true);
            response.put("token", token);
            response.put("user", createdUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Registration failed: " + e.getMessage(), false));
        }
    }
}