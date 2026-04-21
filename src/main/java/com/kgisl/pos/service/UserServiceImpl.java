package com.kgisl.pos.service.impl;

import com.kgisl.pos.entity.User;
import com.kgisl.pos.repository.UserRepository;
import com.kgisl.pos.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole(User.Role.CUSTOMER);
        }
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
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);

        if (updatedUser.getUsername() != null)
            user.setUsername(updatedUser.getUsername());

        if (updatedUser.getPassword() != null)
            user.setPassword(updatedUser.getPassword());

        if (updatedUser.getFullName() != null)
            user.setFullName(updatedUser.getFullName());

        if (updatedUser.getEmail() != null)
            user.setEmail(updatedUser.getEmail());

        if (updatedUser.getRole() != null)
            user.setRole(updatedUser.getRole());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ✅ LOGIN LOGIC
    @Override
    public User login(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email.trim());

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }
}