package com.kgisl.pos.controller;

import com.kgisl.pos.dto.LoginRequest;
import com.kgisl.pos.dto.LoginResponse;
import com.kgisl.pos.entity.User;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Email and password required", false));
        }

        User user = userService.login(request.getEmail(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse("Invalid email or password", false));
        }

        return ResponseEntity.ok(new LoginResponse(user, "Login successful", true));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}