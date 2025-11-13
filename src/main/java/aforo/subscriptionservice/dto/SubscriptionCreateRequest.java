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
}
