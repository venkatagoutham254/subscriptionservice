package aforo.subscriptionservice.service;

import java.util.List;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;

import java.time.LocalDateTime;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(SubscriptionCreateRequest request);
    SubscriptionResponse updateSubscription(Long subscriptionId, SubscriptionUpdateRequest request);
    SubscriptionResponse getSubscription(Long subscriptionId);
    SubscriptionResponse confirmSubscription(Long subscriptionId);
    List<SubscriptionResponse> findAll();
    void delete(Long subscriptionId);

    // Billing cycle operations for Metering Service
    List<SubscriptionResponse> getSubscriptionsEndingBy(LocalDateTime timestamp);

    SubscriptionResponse advanceBillingPeriod(Long subscriptionId);
}
