package com.kgisl.pos.service.impl;

import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.UserRepository;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole(User.Role.CUSTOMER);
        }
        // Validate password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password cannot be null or empty");
        }
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        // Hash password using BCryptPasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);

        if (updatedUser.getUsername() != null)
            user.setUsername(updatedUser.getUsername());

        if (updatedUser.getPassword() != null)
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        if (updatedUser.getFullName() != null)
            user.setFullName(updatedUser.getFullName());

        if (updatedUser.getEmail() != null)
            user.setEmail(updatedUser.getEmail());

        if (updatedUser.getRole() != null)
            user.setRole(updatedUser.getRole());

        if (updatedUser.getAgentId() != null)
            user.setAgentId(updatedUser.getAgentId());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ✅ LOGIN LOGIC - Updated with password encoder
    @Override
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email.trim());

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Use password encoder to compare plain text password with hashed password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    // ✅ UserDetailsService implementation for Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Create Spring Security UserDetails from User entity
        return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
    }
}