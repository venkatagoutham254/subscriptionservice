package aforo.subscriptionservice.service.impl;

import aforo.subscriptionservice.client.CustomerServiceClient;
import aforo.subscriptionservice.client.ProductRatePlanClient;
import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.mapper.SubscriptionMapper;
import aforo.subscriptionservice.repository.SubscriptionRepository;
import aforo.subscriptionservice.service.SubscriptionService;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionMapper mapper;
    private final ProductRatePlanClient productRatePlanClient;
    private final CustomerServiceClient customerServiceClient;


    @Override
    public SubscriptionResponse confirmSubscription(Long subscriptionId) {
        Subscription subscription = repository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setUpdatedAt(LocalDateTime.now());
            repository.save(subscription);
        }
        return mapper.toResponse(subscription);
    }
    @Override
    public SubscriptionResponse createSubscription(SubscriptionCreateRequest request) {
        // ✅ Validate customer first
        customerServiceClient.validateCustomer(request.getCustomerId());
    
        // ✅ Validate product & rate plan existence
        productRatePlanClient.validateProduct(request.getProductId());
        productRatePlanClient.validateRatePlan(request.getRatePlanId());
    
        // ✅ Validate linkage
        productRatePlanClient.validateProductRatePlanLinkage(request.getProductId(), request.getRatePlanId());
    
        Subscription subscription = mapper.toEntity(request);
        return mapper.toResponse(repository.save(subscription));
    }
    

    @Override
    public SubscriptionResponse updateSubscription(Long subscriptionId, SubscriptionUpdateRequest request) {
        Subscription subscription = repository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // ✅ Delegate field updates (including paymentType) to mapper
        mapper.updateEntityFromRequest(request, subscription);

        return mapper.toResponse(repository.save(subscription));
    }

    @Override
    public SubscriptionResponse getSubscription(Long subscriptionId) {
        return repository.findById(subscriptionId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }
}
