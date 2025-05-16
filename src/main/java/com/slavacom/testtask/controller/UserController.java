package com.slavacom.testtask.controller;

import com.slavacom.testtask.entity.User;
import com.slavacom.testtask.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info("GET /users — Retrieving all users");
        var users = userRepository.findAll();
        log.info("GET /users — Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        log.info("POST /users — Creating user with username: {}, email: {}", user.getUsername(), user.getEmail());

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("POST /users — Username '{}' already exists", user.getUsername());
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("POST /users — Email '{}' already exists", user.getEmail());
            return ResponseEntity.badRequest().body("Email already exists");
        }

        user.setId(null);
        User savedUser = userRepository.save(user);
        log.info("POST /users — User created with ID: {}", savedUser.getId());
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        log.info("GET /users/{} — Fetching user by ID", id);
        return userRepository.findById(id)
                .map(user -> {
                    log.info("GET /users/{} — User found", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    log.warn("GET /users/{} — User not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("PUT /users/{} — Updating user with new data: username={}, email={}", id, user.getUsername(), user.getEmail());

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            log.warn("PUT /users/{} — User not found", id);
            return ResponseEntity.status(404).body("User with ID " + id + " not found");
        }

        Optional<User> existUsername = userRepository.findByUsername(user.getUsername());
        if (existUsername.isPresent() && !existUsername.get().getId().equals(id)) {
            log.warn("PUT /users/{} — Username '{}' already used by another user", id, user.getUsername());
            return ResponseEntity.badRequest().body("Username already exists");
        }

        Optional<User> userWithSameEmail = userRepository.findByEmail(user.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
            log.warn("PUT /users/{} — Email '{}' already used by another user", id, user.getEmail());
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User existingUser = existingUserOpt.get();
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        User savedUser = userRepository.save(existingUser);
        log.info("PUT /users/{} — User updated successfully", id);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} — Attempting to delete user", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("DELETE /users/{} — User deleted", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("DELETE /users/{} — User not found", id);
        return ResponseEntity.notFound().build();
    }
}