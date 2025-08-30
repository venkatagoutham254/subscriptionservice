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

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "rate_plan_id")
    private Long ratePlanId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;


    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;


    @Enumerated(EnumType.STRING)   // stores PREPAID/POSTPAID as text
    @Column(name = "payment_type")
    private PaymentType paymentType;

    
    @Column(name = "admin_notes", length = 255)
    private String adminNotes;
}
