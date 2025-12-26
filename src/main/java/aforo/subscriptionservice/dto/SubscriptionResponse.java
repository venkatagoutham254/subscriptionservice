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
    private String customerName;
    private String productName;
    private String ratePlanName;
    private String ratePlanType;
    private Long organizationId;

    // Billing cycle fields
    private LocalDateTime currentBillingPeriodStart;
    private LocalDateTime currentBillingPeriodEnd;
    private LocalDateTime nextBillingTimestamp;
    private String billingAnchorInfo;
    private Boolean autoRenew;
    private String billingFrequency;  // Enriched from rate plan
}
