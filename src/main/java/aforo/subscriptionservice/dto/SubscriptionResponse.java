package aforo.subscriptionservice.dto;

import java.util.UUID;

import aforo.subscriptionservice.entity.PaymentType;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import lombok.*;

import java.time.Instant;

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
    private Instant createdOn;
    private Instant lastUpdated;
    private PaymentType paymentType;

    private String adminNotes;
    private String customerName;
    private String productName;
    private String icon;
    private String ratePlanName;
    private String ratePlanType;
    private Long organizationId;

    // Billing cycle fields
    private Instant currentBillingPeriodStart;
    private Instant currentBillingPeriodEnd;
    private Instant nextBillingTimestamp;
    private String billingAnchorInfo;
    private Boolean autoRenew;
    private String billingFrequency;  // Enriched from rate plan
    
    // Billable metrics fields
    private String billableUid;
    private String usageCondition;
    private String unitOfMeasure;
    private String billingCriteria;
}
