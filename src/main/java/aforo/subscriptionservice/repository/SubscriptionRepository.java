// repository/SubscriptionRepository.java (Primary key: subscriptionId)
package aforo.subscriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aforo.subscriptionservice.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    java.util.Optional<Subscription> findBySubscriptionIdAndOrganizationId(Long subscriptionId, Long organizationId);
    java.util.List<Subscription> findByOrganizationId(Long organizationId);
}
