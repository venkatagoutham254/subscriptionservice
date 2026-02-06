package aforo.subscriptionservice.dto;

import aforo.subscriptionservice.entity.PaymentType;
import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionCreateRequest {

    private Long customerId; 

    

    private Long productId;

    private Long ratePlanId;

    private PaymentType paymentType;



    private String adminNotes;
    
    // Explicit getters (Lombok not configured in this project)
    public Long getCustomerId() {
        return customerId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Long getRatePlanId() {
        return ratePlanId;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
}
