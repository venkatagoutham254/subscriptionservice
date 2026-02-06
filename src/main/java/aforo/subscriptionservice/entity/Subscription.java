package aforo.subscriptionservice.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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
    private Instant createdOn;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Enumerated(EnumType.STRING)   // stores PREPAID/POSTPAID as text
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "admin_notes", length = 255)
    private String adminNotes;

    // ========== BILLING CYCLE TRACKING FIELDS ==========
    
    /**
     * Timestamp when current billing period started (extracted from createdOn)
     * Example: 2025-12-23T15:14:00Z
     */
    @Column(name = "current_billing_period_start")
    private Instant currentBillingPeriodStart;

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
    private Instant currentBillingPeriodEnd;

    /**
     * Timestamp when next billing cycle starts (always = end + 1 second)
     */
    @Column(name = "next_billing_timestamp")
    private Instant nextBillingTimestamp;

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
    
    // Explicit getters (Lombok not configured in this project)
    public Long getSubscriptionId() {
        return subscriptionId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Long getRatePlanId() {
        return ratePlanId;
    }
    
    public SubscriptionStatus getStatus() {
        return status;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public Instant getCreatedOn() {
        return createdOn;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public Instant getCurrentBillingPeriodStart() {
        return currentBillingPeriodStart;
    }
    
    public Instant getCurrentBillingPeriodEnd() {
        return currentBillingPeriodEnd;
    }
    
    public Instant getNextBillingTimestamp() {
        return nextBillingTimestamp;
    }
    
    public String getBillingAnchorInfo() {
        return billingAnchorInfo;
    }
    
    public Boolean getAutoRenew() {
        return autoRenew;
    }
    
    // Explicit setters (Lombok not configured in this project)
    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public void setRatePlanId(Long ratePlanId) {
        this.ratePlanId = ratePlanId;
    }
    
    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }
    
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public void setCurrentBillingPeriodStart(Instant currentBillingPeriodStart) {
        this.currentBillingPeriodStart = currentBillingPeriodStart;
    }
    
    public void setCurrentBillingPeriodEnd(Instant currentBillingPeriodEnd) {
        this.currentBillingPeriodEnd = currentBillingPeriodEnd;
    }
    
    public void setNextBillingTimestamp(Instant nextBillingTimestamp) {
        this.nextBillingTimestamp = nextBillingTimestamp;
    }
    
    public void setBillingAnchorInfo(String billingAnchorInfo) {
        this.billingAnchorInfo = billingAnchorInfo;
    }
    
    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
}
