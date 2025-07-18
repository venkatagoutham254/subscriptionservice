package aforo.subscriptionservice.mapper;

import java.util.UUID;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.entity.Subscription;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionMapper {

    public Subscription toEntity(SubscriptionCreateRequest request) {
        return Subscription.builder()
                .organizationId(request.getOrganizationId()) // UUID
                .divisionId(request.getDivisionId()) // UUID
                .customerId(request.getCustomerId()) // UUID
                .productId(request.getProductId())
                .ratePlanId(request.getRatePlanId())
                .billingFrequency(request.getBillingFrequency())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public SubscriptionResponse toResponse(Subscription entity) {
        return SubscriptionResponse.builder()
                .subscriptionId(entity.getSubscriptionId())
                .organizationId(entity.getOrganizationId()) // UUID
                .divisionId(entity.getDivisionId()) // UUID
                .customerId(entity.getCustomerId()) // UUID
                .productId(entity.getProductId())
                .ratePlanId(entity.getRatePlanId())
                .billingFrequency(entity.getBillingFrequency())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
