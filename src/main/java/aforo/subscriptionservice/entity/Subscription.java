package aforo.subscriptionservice.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    private Long customerId; // Changed from Integer to UUID

    private Long productId;

    private Long ratePlanId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



    @Enumerated(EnumType.STRING)   // stores PREPAID/POSTPAID as text
    @Column(nullable = false)      // required field
    private PaymentType paymentType;

    
    @Column(length = 255)
    private String adminNotes;
}
