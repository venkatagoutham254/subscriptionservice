package aforo.subscriptionservice.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChurnRateResponse {
    
    private Double churnRate; // Percentage
    private Long totalCustomers;
    private Long churnedCustomers;
    private String period; // e.g., "Last 30 days"
}
