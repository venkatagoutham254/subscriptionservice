package aforo.subscriptionservice.controller;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

    // ========== BILLING CYCLE ENDPOINTS FOR METERING SERVICE ==========

    /**
     * Get subscriptions with billing periods ending by specific timestamp
     * Used by Metering Service scheduler to find subscriptions needing invoicing
     */
    @GetMapping("/ending-by")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsEndingBy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsEndingBy(timestamp));
    }

    /**
     * Move subscription to next billing period after invoice is generated
     * Called by Metering Service after creating invoice
     */
    @PatchMapping("/{subscriptionId}/advance-billing-period")
    public ResponseEntity<SubscriptionResponse> advanceBillingPeriod(
            @PathVariable Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.advanceBillingPeriod(subscriptionId));
    }
}
