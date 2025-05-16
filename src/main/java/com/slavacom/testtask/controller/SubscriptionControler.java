package com.slavacom.testtask.controller;


import com.slavacom.testtask.entity.Subscription;
import com.slavacom.testtask.entity.User;
import com.slavacom.testtask.repository.SubscriptionRepository;
import com.slavacom.testtask.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionControler {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionControler(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createSubscription(@Valid @RequestBody Subscription subscription) {
        log.info("POST /subscriptions — Creating subscription with name: {}", subscription.getName());

        if (subscriptionRepository.existsByName(subscription.getName())) {
            log.warn("POST /subscriptions — Subscription name '{}' already exists", subscription.getName());
            return ResponseEntity.badRequest().body("Subscription with this name already exists");
        }

        subscription.setId(null);
        Subscription saved = subscriptionRepository.save(subscription);
        log.info("POST /subscriptions — Subscription created successfully with id: {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubscription(@PathVariable Long id) {
        log.info("GET /subscriptions/{} — Fetching subscription by ID", id);

        return subscriptionRepository.findById(id)
                .map(sub -> {
                    log.info("GET /subscriptions/{} — Subscription found", id);
                    return ResponseEntity.ok(sub);
                })
                .orElseGet(() -> {
                    log.warn("GET /subscriptions/{} — Subscription not found", id);
                    return ResponseEntity.noContent().build();
                });
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        log.info("GET /subscriptions — Retrieving all subscriptions");
        List<Subscription> all = subscriptionRepository.findAll();
        log.info("GET /subscriptions — Found {} subscriptions", all.size());
        return ResponseEntity.ok(all);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubscription(@PathVariable Long id, @RequestBody Subscription subscription) {
        log.info("PUT /subscriptions/{} — Attempting to update subscription", id);

        Optional<Subscription> existingSubscriptionOpt = subscriptionRepository.findById(id);
        if (existingSubscriptionOpt.isEmpty()) {
            log.warn("PUT /subscriptions/{} — Subscription not found", id);
            return ResponseEntity.notFound().build();
        }

        Optional<Subscription> existingName = subscriptionRepository.findByName(subscription.getName());
        if (existingName.isPresent() && !existingName.get().getId().equals(id)) {
            log.warn("PUT /subscriptions/{} — Subscription name '{}' already exists", id, subscription.getName());
            return ResponseEntity.badRequest().body("Subscription with this name already exists");
        }

        Subscription existingSubscription = existingSubscriptionOpt.get();
        existingSubscription.setName(subscription.getName());
        Subscription saved = subscriptionRepository.save(existingSubscription);
        log.info("PUT /subscriptions/{} — Subscription updated successfully", id);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        log.info("DELETE /subscriptions/{} — Attempting to delete subscription", id);

        if (!subscriptionRepository.existsById(id)) {
            log.warn("DELETE /subscriptions/{} — Subscription not found", id);
            return ResponseEntity.notFound().build();
        }

        subscriptionRepository.deleteById(id);
        log.info("DELETE /subscriptions/{} — Subscription deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sub/{subId}/user/{userId}")
    public ResponseEntity<?> addSubscriptionToUser(@PathVariable Long userId, @PathVariable Long subId) {
        log.info("POST /subscriptions/sub/{}/user/{} — Adding subscription to user", subId, userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("POST /subscriptions/sub/{}/user/{} — User not found", subId, userId);
            return ResponseEntity.status(404).body("User not found");
        }

        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subId);
        if (subscriptionOpt.isEmpty()) {
            log.warn("POST /subscriptions/sub/{}/user/{} — Subscription not found", subId, userId);
            return ResponseEntity.status(404).body("Subscription not found");
        }

        User user = userOpt.get();
        Subscription subscription = subscriptionOpt.get();

        if (user.getSubscriptions().contains(subscription)) {
            log.warn("POST /subscriptions/sub/{}/user/{} — User already has this subscription", subId, userId);
            return ResponseEntity.badRequest().body("User already has this subscription");
        }

        user.getSubscriptions().add(subscription);
        userRepository.save(user);
        log.info("POST /subscriptions/sub/{}/user/{} — Subscription added to user", subId, userId);
        return ResponseEntity.ok("Subscription added to user");
    }

    @DeleteMapping("/sub/{subId}/user/{userId}")
    public ResponseEntity<?> removeSubscriptionFromUser(@PathVariable Long userId, @PathVariable Long subId) {
        log.info("DELETE /subscriptions/sub/{}/user/{} — Removing subscription from user", subId, userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("DELETE /subscriptions/sub/{}/user/{} — User not found", subId, userId);
            return ResponseEntity.status(404).body("User not found");
        }

        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subId);
        if (subscriptionOpt.isEmpty()) {
            log.warn("DELETE /subscriptions/sub/{}/user/{} — Subscription not found", subId, userId);
            return ResponseEntity.status(404).body("Subscription not found");
        }

        User user = userOpt.get();
        Subscription subscription = subscriptionOpt.get();

        if (!user.getSubscriptions().contains(subscription)) {
            log.warn("DELETE /subscriptions/sub/{}/user/{} — User does not have this subscription", subId, userId);
            return ResponseEntity.status(400).body("User does not have this subscription");
        }

        user.getSubscriptions().remove(subscription);
        userRepository.save(user);
        log.info("DELETE /subscriptions/sub/{}/user/{} — Subscription removed from user", subId, userId);
        return ResponseEntity.ok("Subscription removed from user");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable Long userId) {
        log.info("GET /subscriptions/user/{} — Fetching user subscriptions", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("GET /subscriptions/user/{} — User not found", userId);
            return ResponseEntity.status(404).body("User not found");
        }

        Set<Subscription> subscriptions = userOpt.get().getSubscriptions();
        log.info("GET /subscriptions/user/{} — Found {} subscriptions", userId, subscriptions.size());
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/top")
    public ResponseEntity<List<Subscription>> getTopSubscriptions() {
        log.info("GET /subscriptions/top — Fetching top 3 popular subscriptions");
        List<Subscription> topSubscriptions = subscriptionRepository.findTopSubscriptions(PageRequest.of(0, 3));
        log.info("GET /subscriptions/top — Retrieved {} subscriptions", topSubscriptions.size());
        return ResponseEntity.ok(topSubscriptions);
    }
}
