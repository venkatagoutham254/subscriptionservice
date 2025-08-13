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
    private UUID customerId; // Changed from Integer to UUID
    private Integer productId;
    private Integer ratePlanId;
    private SubscriptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String adminNotes;
}
