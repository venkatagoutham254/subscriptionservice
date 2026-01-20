package aforo.subscriptionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillableMetricDTO {
    private Long billableMetricId;
    private String metricName;
    private Long productId;
    private String productName;
    private String version;
    private String unitOfMeasure;
    private String description;
    private String aggregationFunction;
    private String aggregationWindow;
    private List<UsageConditionDTO> usageConditions;
    private String billingCriteria;
    private String status;
    private String createdOn;
    private String lastUpdated;
    private Long organizationId;
    
    // Helper methods to get billableUid and usageCondition for backward compatibility
    public String getBillableUid() {
        return billableMetricId != null ? String.valueOf(billableMetricId) : null;
    }
    
    public String getUsageCondition() {
        if (usageConditions != null && !usageConditions.isEmpty()) {
            StringBuilder conditions = new StringBuilder();
            for (int i = 0; i < usageConditions.size(); i++) {
                UsageConditionDTO condition = usageConditions.get(i);
                conditions.append(condition.getDimension())
                         .append(" ")
                         .append(condition.getOperator())
                         .append(" ")
                         .append(condition.getValue());
                if (i < usageConditions.size() - 1) {
                    conditions.append(" AND ");
                }
            }
            return conditions.toString();
        }
        return null;
    }
}
