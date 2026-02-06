package aforo.subscriptionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageConditionDTO {
    private String dimension;
    private String operator;
    private String value;
    
    // Explicit getters (Lombok not configured in this project)
    public String getDimension() {
        return dimension;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public String getValue() {
        return value;
    }
}
