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
                .createdOn(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .adminNotes(request.getAdminNotes())
                .build();
    }

    public void updateEntityFromRequest(SubscriptionUpdateRequest request, Subscription entity) {
        if (request.getPaymentType() != null) {
            entity.setPaymentType(request.getPaymentType());
        }
        if (request.getCustomerId() != null) {
            entity.setCustomerId(request.getCustomerId());
        }
        if (request.getProductId() != null) {
            entity.setProductId(request.getProductId());
        }
        if (request.getRatePlanId() != null) {
            entity.setRatePlanId(request.getRatePlanId());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getAdminNotes() != null) {
            entity.setAdminNotes(request.getAdminNotes());
        }
        entity.setLastUpdated(LocalDateTime.now());
    }

    public SubscriptionResponse toResponse(Subscription entity) {
        return SubscriptionResponse.builder()
                .subscriptionId(entity.getSubscriptionId())
                .customerId(entity.getCustomerId())
                .productId(entity.getProductId())
                .ratePlanId(entity.getRatePlanId())
                .paymentType(entity.getPaymentType())
                .status(entity.getStatus())
                .createdOn(entity.getCreatedOn())
                .lastUpdated(entity.getLastUpdated())
                .adminNotes(entity.getAdminNotes())
                .build();
    }
}
