package aforo.subscriptionservice.mapper;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionMapper {

    public Subscription toEntity(SubscriptionCreateRequest request) {
        return Subscription.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .ratePlanId(request.getRatePlanId())
                .paymentType(request.getPaymentType())
                .status(SubscriptionStatus.DRAFT)   // default status on create
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .adminNotes(request.getAdminNotes())
                .build();
    }

    public void updateEntityFromRequest(SubscriptionUpdateRequest request, Subscription entity) {
        if (request.getPaymentType() != null) {
            entity.setPaymentType(request.getPaymentType());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getAdminNotes() != null) {
            entity.setAdminNotes(request.getAdminNotes());
        }
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public SubscriptionResponse toResponse(Subscription entity) {
        return SubscriptionResponse.builder()
                .subscriptionId(entity.getSubscriptionId())
                .customerId(entity.getCustomerId())
                .productId(entity.getProductId())
                .ratePlanId(entity.getRatePlanId())
                .paymentType(entity.getPaymentType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .adminNotes(entity.getAdminNotes())
                .build();
    }
}
