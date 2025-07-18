package aforo.subscriptionservice.service.impl;

import aforo.subscriptionservice.client.OrganizationClient;
import aforo.subscriptionservice.client.ProductRatePlanClient;
import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.mapper.SubscriptionMapper;
import aforo.subscriptionservice.repository.SubscriptionRepository;
import aforo.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionMapper mapper;

    private final OrganizationClient organizationClient;
    private final ProductRatePlanClient productRatePlanClient;

    @Override
    public SubscriptionResponse createSubscription(SubscriptionCreateRequest request) {
        // ✅ Validate all required references
        organizationClient.validateOrganization(request.getOrganizationId());

        if (request.getDivisionId() != null) {
            organizationClient.validateDivision(request.getDivisionId());
        }

        organizationClient.validateCustomer(request.getCustomerId());
        productRatePlanClient.validateProduct(request.getProductId());
        productRatePlanClient.validateRatePlan(request.getRatePlanId());

        Subscription subscription = mapper.toEntity(request);
        return mapper.toResponse(repository.save(subscription));
    }

    @Override
    public SubscriptionResponse updateSubscription(Long subscriptionId, SubscriptionUpdateRequest request) {
        Subscription subscription = repository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (request.getEndDate() != null) {
            subscription.setEndDate(request.getEndDate());
        }

        if (request.getStatus() != null) {
            subscription.setStatus(request.getStatus());
        }

        subscription.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(subscription));
    }

    @Override
    public SubscriptionResponse getSubscription(Long subscriptionId) {
        return repository.findById(subscriptionId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }
}
