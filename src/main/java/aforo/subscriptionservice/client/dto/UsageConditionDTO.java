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
}
