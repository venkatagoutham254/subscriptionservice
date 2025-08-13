package aforo.subscriptionservice.mapper;



import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.entity.Subscription;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionMapper {

    public Subscription toEntity(SubscriptionCreateRequest request) {
        return Subscription.builder()
                .customerId(request.getCustomerId()) // UUID
                .productId(request.getProductId())
                .ratePlanId(request.getRatePlanId())
                .status(aforo.subscriptionservice.entity.SubscriptionStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .adminNotes(request.getAdminNotes())
                .build();
    }

    public SubscriptionResponse toResponse(Subscription entity) {
        return SubscriptionResponse.builder()
                .subscriptionId(entity.getSubscriptionId())
                .customerId(entity.getCustomerId()) // UUID
                .productId(entity.getProductId())
                .ratePlanId(entity.getRatePlanId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .adminNotes(entity.getAdminNotes())
                .build();
    }
}
