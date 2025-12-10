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

	private FlatFeeDTO flatFee;
	private List<VolumePricingDTO> volumePricings;
	private List<UsageBasedPricingDTO> usageBasedPricings;
	private List<TieredPricingDTO> tieredPricings;
	private List<StairStepPricingDTO> stairStepPricings;
}
