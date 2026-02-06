package aforo.subscriptionservice.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHealthResponse {
    
    private Long customerId;
    private Integer subscriptionHealthScore; // 0-100
    private Long activeSubscriptions;
    private Long totalSubscriptions;
    private Boolean hasActiveSubscription;
    private Boolean hasOverdueRenewal;
    private String status; // "HEALTHY", "AT_RISK", "CHURNED"
}
