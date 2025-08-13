package aforo.subscriptionservice.assembler;



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
