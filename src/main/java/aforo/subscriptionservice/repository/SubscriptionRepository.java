// repository/SubscriptionRepository.java (Primary key: subscriptionId)
package aforo.subscriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.entity.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    java.util.Optional<Subscription> findBySubscriptionIdAndOrganizationId(Long subscriptionId, Long organizationId);
    java.util.List<Subscription> findByOrganizationId(Long organizationId);

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
        @Param("timestamp") LocalDateTime timestamp,
        @Param("status") SubscriptionStatus status
    );
}
