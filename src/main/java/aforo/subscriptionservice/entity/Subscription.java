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

    private UUID organizationId; // Changed from Integer to UUID

    private UUID divisionId; // Changed from Integer to UUID, Nullable

    private UUID customerId; // Changed from Integer to UUID

    private Integer productId;

    private Integer ratePlanId;

    private Integer billingFrequency;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
