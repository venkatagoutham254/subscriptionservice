package aforo.subscriptionservice.dto;

import aforo.subscriptionservice.entity.PaymentType;
import aforo.subscriptionservice.entity.SubscriptionStatus; // or enums
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for updating a subscription.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionUpdateRequest {

    private String adminNotes;

    private SubscriptionStatus status;

    private PaymentType paymentType;

    private Long customerId;

    private Long productId;

    private Long ratePlanId;

}
