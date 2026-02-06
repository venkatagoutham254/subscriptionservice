// repository/SubscriptionRepository.java (Primary key: subscriptionId)
package aforo.subscriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.entity.SubscriptionStatus;

import java.time.Instant;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    java.util.Optional<Subscription> findBySubscriptionIdAndOrganizationId(Long subscriptionId, Long organizationId);
    java.util.List<Subscription> findByOrganizationId(Long organizationId);
    java.util.List<Subscription> findByCustomerIdAndOrganizationId(Long customerId, Long organizationId);

    /**
     * Find subscriptions where billing period has ended (or will end) by specific timestamp
     * Used by Metering Service scheduler to find subscriptions needing invoicing
     * 
     * @param timestamp The cutoff timestamp to check against
     * @param status Subscription status filter (typically ACTIVE)
     * @return List of subscriptions with billing periods ending by the timestamp
     */
    @Query("SELECT s FROM Subscription s " +
           "WHERE s.currentBillingPeriodEnd <= :timestamp " +
           "AND s.status = :status " +
           "AND s.autoRenew = true")
    List<Subscription> findSubscriptionsEndingByTimestamp(
        @Param("timestamp") Instant timestamp,
        @Param("status") SubscriptionStatus status
    );

    // ========== ANALYTICS QUERIES ==========

    /**
     * Count active subscriptions by organization
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.organizationId = :organizationId AND s.status = 'ACTIVE'")
    Long countActiveSubscriptions(@Param("organizationId") Long organizationId);

    /**
     * Count subscriptions by customer ID
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * Count active subscriptions by customer ID
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.customerId = :customerId AND s.status = 'ACTIVE'")
    Long countActiveByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find subscriptions by customer and organization
     */
    List<Subscription> findByCustomerIdAndOrganizationId(Long customerId, Long organizationId);

    /**
     * Find all subscriptions for a list of customer IDs
     */
    @Query("SELECT s FROM Subscription s WHERE s.customerId IN :customerIds AND s.organizationId = :organizationId")
    List<Subscription> findByCustomerIdsAndOrganizationId(
        @Param("customerIds") List<Long> customerIds,
        @Param("organizationId") Long organizationId
    );

    /**
     * Count subscriptions that became INACTIVE in a date range (for churn calculation)
     */
    @Query("SELECT COUNT(DISTINCT s.customerId) FROM Subscription s " +
           "WHERE s.organizationId = :organizationId " +
           "AND s.status = 'INACTIVE' " +
           "AND s.lastUpdated BETWEEN :startDate AND :endDate")
    Long countChurnedCustomers(
        @Param("organizationId") Long organizationId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get distinct customer IDs with subscriptions by organization
     */
    @Query("SELECT DISTINCT s.customerId FROM Subscription s WHERE s.organizationId = :organizationId")
    List<Long> findDistinctCustomerIds(@Param("organizationId") Long organizationId);
}
