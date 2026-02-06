package aforo.subscriptionservice.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsageBasedPricingDTO {
    private Long usageBasedPricingId;
    private Long ratePlanId;
    private Double perUnitAmount;
    private String ratePlanType;
    
    // Explicit getter for ratePlanType
    public String getRatePlanType() {
        return ratePlanType;
    }
}
