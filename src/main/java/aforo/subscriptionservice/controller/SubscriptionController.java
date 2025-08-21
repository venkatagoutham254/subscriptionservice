package aforo.subscriptionservice.controller;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@RequestBody @Valid SubscriptionCreateRequest request) {
        return ResponseEntity.ok(subscriptionService.createSubscription(request));
    }

    @PatchMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponse> update(
            @PathVariable Long subscriptionId,
            @RequestBody @Valid SubscriptionUpdateRequest request) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(subscriptionId, request));
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponse> get(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.getSubscription(subscriptionId));
    }

    @PostMapping("/{subscriptionId}/confirm")
    public ResponseEntity<SubscriptionResponse> confirm(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.confirmSubscription(subscriptionId));
    }

    @GetMapping
    public List<SubscriptionResponse> getAll() {
        return subscriptionService.findAll();
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> delete(@PathVariable Long subscriptionId) {
        subscriptionService.delete(subscriptionId);
        return ResponseEntity.noContent().build(); // 204
    }
}
