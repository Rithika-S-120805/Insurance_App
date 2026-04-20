package com.kgisl.pos.controller;

import com.kgisl.pos.dto.LoginRequest;
import com.kgisl.pos.dto.LoginResponse;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Login endpoint - Authenticates user credentials against USER table
     * 
     * @param request LoginRequest containing email and password
     * @return ResponseEntity with LoginResponse (user details if successful, error if failed)
     */
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        System.out.println("\n=== LOGIN REQUEST ===");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + request.getPassword());

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse("Email is required", false));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse("Password is required", false));
        }

        // ✅ FIXED DEBUG
        List<User> allUsers = userService.getAllUsers();
        System.out.println("Total users in DB: " + allUsers.size());
        allUsers.forEach(u -> System.out.println(
                "Email: " + u.getEmail() +
                " | Username: " + u.getUsername() +
                " | Role: " + u.getRole()
        ));

        User authenticatedUser = userService.login(request.getEmail(), request.getPassword());

        if (authenticatedUser != null) {
            return ResponseEntity.ok(new LoginResponse(authenticatedUser, "Login successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Invalid email or password", false));
        }

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
        System.out.println("\n=== TEST ENDPOINT ===");
        System.out.println("Total users in DB: " + allUsers.size());
        allUsers.forEach(u -> System.out.println("  - " + u.getEmail() + " | Username: " + u.getUsername()));
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", allUsers.size());
        response.put("users", allUsers);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID endpoint
     * 
     * @param id User ID
     * @return ResponseEntity with user details
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            System.out.println("\n=== GET USER BY ID ===");
            System.out.println("Requested ID: " + id);
            
            // Debug: Show all users in DB
            List<User> allUsers = userService.getAllUsers();
            System.out.println("Total users in DB: " + allUsers.size());
            allUsers.forEach(u -> System.out.println("  - ID: " + u.getUserId() + " | Email: " + u.getEmail()));
            
            User user = userService.getUserById(id);
            System.out.println("✅ User found: " + user.getEmail());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            System.out.println("❌ User not found for ID: " + id);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoginResponse("User not found with ID: " + id, false));
        }
    }

    /**
     * Register new user endpoint
     * 
     * @param user User object with registration details
     * @return ResponseEntity with created user
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

    // ❌ REMOVED isActive

    try {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(createdUser, "User registered successfully", true));
    } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body(new LoginResponse("Registration failed", false));
    }
}
}