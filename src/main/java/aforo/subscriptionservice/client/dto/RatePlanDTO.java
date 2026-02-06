package aforo.subscriptionservice.client.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatePlanDTO {

	private Long ratePlanId;
	private Long productId;
	private String ratePlanName;
	private String ratePlanType;
	private String billingFrequency;  // HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY

	private FlatFeeDTO flatFee;
	private List<VolumePricingDTO> volumePricings;
	private List<UsageBasedPricingDTO> usageBasedPricings;
	private List<TieredPricingDTO> tieredPricings;
	private List<StairStepPricingDTO> stairStepPricings;

	// Explicit getters for fields (Lombok not configured in this project)
	public FlatFeeDTO getFlatFee() {
		return flatFee;
	}

	public List<VolumePricingDTO> getVolumePricings() {
		return volumePricings;
	}

	public List<UsageBasedPricingDTO> getUsageBasedPricings() {
		return usageBasedPricings;
	}

	public List<TieredPricingDTO> getTieredPricings() {
		return tieredPricings;
	}

	public List<StairStepPricingDTO> getStairStepPricings() {
		return stairStepPricings;
	}
	
	// Additional explicit getters
	public Long getRatePlanId() {
		return ratePlanId;
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public String getRatePlanName() {
		return ratePlanName;
	}
	
	public String getRatePlanType() {
		return ratePlanType;
	}
	
	public String getBillingFrequency() {
		return billingFrequency;
	}
}
