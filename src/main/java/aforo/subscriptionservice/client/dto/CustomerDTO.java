package aforo.subscriptionservice.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {
    private Long customerId;
    private String customerName;
    
    // Explicit getters (Lombok not configured in this project)
    public Long getCustomerId() {
        return customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
}
