package aforo.subscriptionservice.assembler;

import java.util.UUID;

import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.entity.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionAssembler {
    public SubscriptionResponse fromEntity(Subscription entity) {
        return SubscriptionResponse.builder()
                .subscriptionId(entity.getSubscriptionId())
                .organizationId(entity.getOrganizationId()) // UUID
                .divisionId(entity.getDivisionId()) // UUID
                .customerId(entity.getCustomerId()) // UUID
                .productId(entity.getProductId())
                .ratePlanId(entity.getRatePlanId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .billingFrequency(entity.getBillingFrequency())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
