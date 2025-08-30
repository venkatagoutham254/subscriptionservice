package aforo.subscriptionservice.dto;

import java.util.UUID;

import aforo.subscriptionservice.entity.PaymentType;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long subscriptionId;
    private Long customerId; // Changed from Integer to UUID
    private Long productId;
    private Long ratePlanId;
    private SubscriptionStatus status;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;
    private PaymentType paymentType;

    private String adminNotes;
}
