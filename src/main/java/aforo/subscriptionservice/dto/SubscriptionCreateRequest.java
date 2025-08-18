package aforo.subscriptionservice.dto;

import java.util.UUID;

import aforo.subscriptionservice.entity.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionCreateRequest {

    @NotNull
    private Long customerId; 

    

    @NotNull
    private Long productId;

    @NotNull
    private Long ratePlanId;

@NotNull(message = "Payment type is required")
private PaymentType paymentType;



    private String adminNotes;
}
