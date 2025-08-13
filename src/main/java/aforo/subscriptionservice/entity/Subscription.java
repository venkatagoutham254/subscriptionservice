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

    private UUID customerId; // Changed from Integer to UUID

    private Integer productId;

    private Integer ratePlanId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(length = 255)
    private String adminNotes;
}
