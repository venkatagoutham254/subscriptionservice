package aforo.subscriptionservice.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMrrResponse {
    
    private List<CustomerMrr> customers;
    private Double totalMrr;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerMrr {
        private Long customerId;
        private Long activeSubscriptions;
        private Double monthlyRecurringRevenue;
        private String subscriptionStatus; // "ACTIVE", "SUSPENDED", etc.
    }
}
