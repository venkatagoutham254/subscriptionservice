package aforo.subscriptionservice.analytics.service;

import aforo.subscriptionservice.analytics.dto.*;
import aforo.subscriptionservice.client.ProductRatePlanClient;
import aforo.subscriptionservice.client.dto.RatePlanDTO;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionAnalyticsService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProductRatePlanClient ratePlanClient;

    /**
     * Calculate churn rate for an organization
     */
    public ChurnRateResponse getChurnRate(Long organizationId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating churn rate for org: {} from {} to {}", organizationId, startDate, endDate);

        // Get total unique customers with subscriptions
        List<Long> allCustomerIds = subscriptionRepository.findDistinctCustomerIds(organizationId);
        long totalCustomers = allCustomerIds.size();

        // Count customers who churned (subscriptions became INACTIVE) in the period
        long churnedCustomers = subscriptionRepository.countChurnedCustomers(organizationId, startDate, endDate);

        // Calculate churn rate
        double churnRate = totalCustomers > 0 ? (churnedCustomers * 100.0 / totalCustomers) : 0.0;

        String period = String.format("%s to %s", 
            startDate.toLocalDate(), 
            endDate.toLocalDate());

        return ChurnRateResponse.builder()
                .churnRate(churnRate)
                .totalCustomers(totalCustomers)
                .churnedCustomers(churnedCustomers)
                .period(period)
                .build();
    }

    /**
     * Get MRR (Monthly Recurring Revenue) by customer
     */
    public CustomerMrrResponse getMrrByCustomer(Long organizationId, List<Long> customerIds) {
        log.debug("Calculating MRR for org: {} and {} customers", organizationId, customerIds != null ? customerIds.size() : "all");

        List<Long> targetCustomerIds = customerIds;
        if (targetCustomerIds == null || targetCustomerIds.isEmpty()) {
            targetCustomerIds = subscriptionRepository.findDistinctCustomerIds(organizationId);
        }

        List<Subscription> subscriptions = subscriptionRepository.findByCustomerIdsAndOrganizationId(
            targetCustomerIds, organizationId);

        // Group subscriptions by customer
        Map<Long, List<Subscription>> subscriptionsByCustomer = subscriptions.stream()
                .collect(Collectors.groupingBy(Subscription::getCustomerId));

        List<CustomerMrrResponse.CustomerMrr> customerMrrList = new ArrayList<>();
        double totalMrr = 0.0;

        for (Long customerId : targetCustomerIds) {
            List<Subscription> customerSubscriptions = subscriptionsByCustomer.getOrDefault(customerId, new ArrayList<>());
            
            long activeCount = customerSubscriptions.stream()
                    .filter(s -> "ACTIVE".equals(s.getStatus().name()))
                    .count();

            // Calculate MRR for this customer
            // Note: This is simplified - in reality, you'd get pricing from RatePlan
            // For now, we'll estimate based on active subscriptions
            double customerMrr = calculateCustomerMrr(customerSubscriptions);
            totalMrr += customerMrr;

            String status = activeCount > 0 ? "ACTIVE" : "INACTIVE";

            customerMrrList.add(CustomerMrrResponse.CustomerMrr.builder()
                    .customerId(customerId)
                    .activeSubscriptions(activeCount)
                    .monthlyRecurringRevenue(customerMrr)
                    .subscriptionStatus(status)
                    .build());
        }

        return CustomerMrrResponse.builder()
                .customers(customerMrrList)
                .totalMrr(totalMrr)
                .build();
    }

    /**
     * Get subscription health score for a customer
     */
    public SubscriptionHealthResponse getCustomerHealth(Long customerId, Long organizationId) {
        log.debug("Calculating subscription health for customer: {}", customerId);

        List<Subscription> subscriptions = subscriptionRepository.findByCustomerIdAndOrganizationId(
            customerId, organizationId);

        long totalSubscriptions = subscriptions.size();
        long activeSubscriptions = subscriptions.stream()
                .filter(s -> "ACTIVE".equals(s.getStatus().name()))
                .count();

        boolean hasActive = activeSubscriptions > 0;
        boolean hasOverdue = checkForOverdueRenewals(subscriptions);

        // Calculate health score based on subscription status
        int healthScore = calculateSubscriptionHealthScore(activeSubscriptions, totalSubscriptions, hasOverdue);
        String status = determineHealthStatus(healthScore);

        return SubscriptionHealthResponse.builder()
                .customerId(customerId)
                .subscriptionHealthScore(healthScore)
                .activeSubscriptions(activeSubscriptions)
                .totalSubscriptions(totalSubscriptions)
                .hasActiveSubscription(hasActive)
                .hasOverdueRenewal(hasOverdue)
                .status(status)
                .build();
    }

    /**
     * Get active subscription count for organization
     */
    public Long getActiveSubscriptionCount(Long organizationId) {
        return subscriptionRepository.countActiveSubscriptions(organizationId);
    }

    // ========== HELPER METHODS ==========

    private double calculateCustomerMrr(List<Subscription> subscriptions) {
        // Simplified MRR calculation
        // In production, you would:
        // 1. Fetch RatePlan for each subscription via ratePlanClient.getRatePlan()
        // 2. Extract pricing information
        // 3. Normalize to monthly value based on billing frequency
        
        long activeSubscriptions = subscriptions.stream()
                .filter(s -> "ACTIVE".equals(s.getStatus().name()))
                .count();

        // Placeholder: Assume average MRR of $100 per active subscription
        // TODO: Replace with real pricing from RatePlan
        return activeSubscriptions * 100.0;
    }

    private boolean checkForOverdueRenewals(List<Subscription> subscriptions) {
        Instant now = Instant.now();
        return subscriptions.stream()
                .anyMatch(s -> s.getNextBillingTimestamp() != null && 
                              s.getNextBillingTimestamp().isBefore(now) &&
                              "ACTIVE".equals(s.getStatus().name()));
    }

    private int calculateSubscriptionHealthScore(long active, long total, boolean hasOverdue) {
        if (total == 0) {
            return 0; // No subscriptions
        }

        // Base score on active subscription ratio
        double activeRatio = (double) active / total;
        int baseScore = (int) (activeRatio * 100);

        // Penalty for overdue renewals
        if (hasOverdue) {
            baseScore -= 30;
        }

        // Ensure score is between 0-100
        return Math.max(0, Math.min(100, baseScore));
    }

    private String determineHealthStatus(int healthScore) {
        if (healthScore >= 70) {
            return "HEALTHY";
        } else if (healthScore >= 40) {
            return "AT_RISK";
        } else {
            return "CHURNED";
        }
    }
}
