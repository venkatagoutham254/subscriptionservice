package aforo.subscriptionservice.dto;

import java.util.UUID;

import aforo.subscriptionservice.entity.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionCreateRequest {

    @NotNull
    private UUID organizationId; // Changed from Integer to UUID

    private UUID divisionId; // Changed from Integer to UUID

    @NotNull
    private UUID customerId; // Changed from Integer to UUID

    @NotNull
    private Integer productId;

    @NotNull
    private Integer ratePlanId;

    @NotNull
    private Integer billingFrequency;

    @NotNull
    private SubscriptionStatus status;

    @NotNull
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startDate;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endDate;
}
