package aforo.subscriptionservice.dto;

import java.util.UUID;


import jakarta.validation.constraints.NotNull;
import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionCreateRequest {

    @NotNull
    private UUID customerId; 

    

    @NotNull
    private Integer productId;

    @NotNull
    private Integer ratePlanId;




    private String adminNotes;
}
