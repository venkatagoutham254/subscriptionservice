package aforo.subscriptionservice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumePricingDTO {
    private String ratePlanType;

    // Explicit getter (Lombok not configured in this project)
    public String getRatePlanType() {
        return ratePlanType;
    }
}
