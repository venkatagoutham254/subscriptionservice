package aforo.subscriptionservice.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "rate_plan_id")
    private Long ratePlanId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)   // stores PREPAID/POSTPAID as text
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "admin_notes", length = 255)
    private String adminNotes;

    // ========== BILLING CYCLE TRACKING FIELDS ==========
    
    /**
     * Timestamp when current billing period started (extracted from createdOn)
     * Example: 2025-12-23T15:14:00
     */
    @Column(name = "current_billing_period_start")
    private LocalDateTime currentBillingPeriodStart;

    /**
     * Timestamp when current billing period ends
     * Calculated based on billing frequency from rate plan
     * For HOURLY: start + 1 hour - 1 second
     * For DAILY: start + 1 day - 1 second
     * For WEEKLY: start + 7 days - 1 second
     * For MONTHLY: start + 1 month - 1 second
     * For YEARLY: start + 1 year - 1 second
     */
    @Column(name = "current_billing_period_end")
    private LocalDateTime currentBillingPeriodEnd;

    /**
     * Timestamp when next billing cycle starts (always = end + 1 second)
     */
    @Column(name = "next_billing_timestamp")
    private LocalDateTime nextBillingTimestamp;

    /**
     * Human-readable billing schedule info for display
     * Examples: "Every hour at 14 minutes past", "Daily at 15:14", "Monthly on day 23"
     */
    @Column(name = "billing_anchor_info", length = 100)
    private String billingAnchorInfo;

    /**
     * Whether subscription automatically renews and generates invoices at end of billing period
     */
    @Column(name = "auto_renew")
    private Boolean autoRenew;
}
