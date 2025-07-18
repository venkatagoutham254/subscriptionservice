package aforo.subscriptionservice.dto;

import aforo.subscriptionservice.entity.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionUpdateRequest {

    private LocalDateTime endDate;

    private SubscriptionStatus status;
}
