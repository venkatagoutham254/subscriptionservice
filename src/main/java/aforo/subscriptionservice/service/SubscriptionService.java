package aforo.subscriptionservice.service;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(SubscriptionCreateRequest request);
    SubscriptionResponse updateSubscription(Long subscriptionId, SubscriptionUpdateRequest request);
    SubscriptionResponse getSubscription(Long subscriptionId);
    SubscriptionResponse confirmSubscription(Long subscriptionId);
}
