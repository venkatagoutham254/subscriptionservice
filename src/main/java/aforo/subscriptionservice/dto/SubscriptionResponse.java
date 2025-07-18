package aforo.subscriptionservice.dto;

import java.util.UUID;

import aforo.subscriptionservice.entity.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long subscriptionId;
    private UUID organizationId; // Changed from Integer to UUID
    private UUID divisionId; // Changed from Integer to UUID
    private UUID customerId; // Changed from Integer to UUID
    private Integer productId;
    private Integer ratePlanId;
    private Integer billingFrequency;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
