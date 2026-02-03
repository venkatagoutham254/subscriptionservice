package aforo.subscriptionservice.service.impl;

import aforo.subscriptionservice.client.CustomerServiceClient;
import aforo.subscriptionservice.client.ProductRatePlanClient;
import aforo.subscriptionservice.client.BillableMetricsClient;
import aforo.subscriptionservice.client.dto.CustomerDTO;
import aforo.subscriptionservice.client.dto.ProductDTO;
import aforo.subscriptionservice.client.dto.RatePlanDTO;
import aforo.subscriptionservice.client.dto.BillableMetricDTO;
import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.mapper.SubscriptionMapper;
import aforo.subscriptionservice.repository.SubscriptionRepository;
import aforo.subscriptionservice.service.SubscriptionService;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import aforo.subscriptionservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    // UTC timezone for display formatting only
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    private final SubscriptionRepository repository;
    private final SubscriptionMapper mapper;
    private final ProductRatePlanClient productRatePlanClient;
    private final CustomerServiceClient customerServiceClient;
    private final BillableMetricsClient billableMetricsClient;

    private SubscriptionResponse enrich(Subscription subscription) {
        SubscriptionResponse resp = mapper.toResponse(subscription);
        try {
            if (subscription.getCustomerId() != null) {
                CustomerDTO c = customerServiceClient.getCustomer(subscription.getCustomerId());
                if (c != null) resp.setCustomerName(c.getCustomerName());
            }
        } catch (Exception ignored) {}
        try {
            if (subscription.getProductId() != null) {
                ProductDTO p = productRatePlanClient.getProduct(subscription.getProductId());
                if (p != null) {
                    resp.setProductName(p.getProductName());
                    resp.setIcon(p.getIcon());
                }
            }
        } catch (Exception ignored) {}
        try {
            if (subscription.getRatePlanId() != null) {
                RatePlanDTO rp = productRatePlanClient.getRatePlan(subscription.getRatePlanId());
                if (rp != null) {
                    resp.setRatePlanName(rp.getRatePlanName());

                    String ratePlanType = rp.getRatePlanType();

                    if (ratePlanType == null) {
                        if (rp.getUsageBasedPricings() != null && !rp.getUsageBasedPricings().isEmpty()
                                && rp.getUsageBasedPricings().get(0).getRatePlanType() != null) {
                            ratePlanType = rp.getUsageBasedPricings().get(0).getRatePlanType();
                        } else if (rp.getTieredPricings() != null && !rp.getTieredPricings().isEmpty()
                                && rp.getTieredPricings().get(0).getRatePlanType() != null) {
                            ratePlanType = rp.getTieredPricings().get(0).getRatePlanType();
                        } else if (rp.getVolumePricings() != null && !rp.getVolumePricings().isEmpty()
                                && rp.getVolumePricings().get(0).getRatePlanType() != null) {
                            ratePlanType = rp.getVolumePricings().get(0).getRatePlanType();
                        } else if (rp.getStairStepPricings() != null && !rp.getStairStepPricings().isEmpty()
                                && rp.getStairStepPricings().get(0).getRatePlanType() != null) {
                            ratePlanType = rp.getStairStepPricings().get(0).getRatePlanType();
                        } else if (rp.getFlatFee() != null && rp.getFlatFee().getRatePlanType() != null) {
                            ratePlanType = rp.getFlatFee().getRatePlanType();
                        }
                    }

                    if (ratePlanType != null) {
                        resp.setRatePlanType(ratePlanType);
                    }
                    // Enrich with billing frequency
                    if (rp.getBillingFrequency() != null) {
                        resp.setBillingFrequency(rp.getBillingFrequency());
                    }
                }
            }
        } catch (Exception ignored) {}
        try {
            if (subscription.getProductId() != null) {
                List<BillableMetricDTO> billableMetrics = billableMetricsClient.getBillableMetricsByProduct(subscription.getProductId());
                if (billableMetrics != null && !billableMetrics.isEmpty()) {
                    BillableMetricDTO firstMetric = billableMetrics.get(0);
                    resp.setBillableUid(firstMetric.getBillableUid());
                    resp.setUsageCondition(firstMetric.getUsageCondition());
                    resp.setUnitOfMeasure(firstMetric.getUnitOfMeasure());
                    resp.setBillingCriteria(firstMetric.getBillingCriteria());
                }
            }
        } catch (Exception ignored) {}
        return resp;
    }

    @Override
    public SubscriptionResponse confirmSubscription(Long subscriptionId) {
        Long orgId = TenantContext.require();
        Subscription subscription = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        // Validate required fields before activation
        StringBuilder missing = new StringBuilder();
        if (subscription.getCustomerId() == null) missing.append("customerId,");
        if (subscription.getProductId() == null) missing.append("productId,");
        if (subscription.getRatePlanId() == null) missing.append("ratePlanId,");
        if (subscription.getPaymentType() == null) missing.append("paymentType,");

        if (missing.length() > 0) {
            // trim trailing comma
            String msg = "Cannot confirm subscription: missing required fields [" + missing.substring(0, missing.length()-1) + "]";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setLastUpdated(Instant.now());
            subscription = repository.save(subscription);
        }
        return enrich(subscription);
    }

    @Override
    public SubscriptionResponse createSubscription(SubscriptionCreateRequest request) {
        Long orgId = TenantContext.require();
        
        // Validate only when provided (allow nulls end-to-end)
        if (request.getCustomerId() != null) {
            customerServiceClient.validateCustomer(request.getCustomerId());
        }
        if (request.getProductId() != null) {
            productRatePlanClient.validateProduct(request.getProductId());
        }
        
        // Fetch rate plan if ratePlanId is provided (validate and extract billing frequency)
        RatePlanDTO ratePlan = null;
        String billingFrequency = null;
        
        if (request.getRatePlanId() != null) {
            // Fetch rate plan - this validates it exists and gives us billing frequency
            ratePlan = productRatePlanClient.getRatePlan(request.getRatePlanId());
            billingFrequency = ratePlan.getBillingFrequency();
            
            // Validate product-rateplan linkage if both are provided
            if (request.getProductId() != null) {
                productRatePlanClient.validateProductRatePlanLinkage(request.getProductId(), request.getRatePlanId());
            }
        }

        // Create subscription entity
        Subscription subscription = mapper.toEntity(request);
        subscription.setOrganizationId(orgId);
        subscription.setAutoRenew(true);  // Default to auto-renew
        
        // Save first to get createdOn timestamp populated
        subscription = repository.save(subscription);
        
        // Calculate and set billing cycle using billing frequency from rate plan
        if (billingFrequency != null && !billingFrequency.isBlank()) {
            calculateAndSetBillingCycle(subscription, billingFrequency);
            subscription = repository.save(subscription);
        }
        
        return enrich(subscription);
    }

    @Override
    public SubscriptionResponse updateSubscription(Long subscriptionId, SubscriptionUpdateRequest request) {
        Long orgId = TenantContext.require();
        Subscription subscription = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        // Validate present fields only (PATCH semantics)
        if (request.getCustomerId() != null) {
            customerServiceClient.validateCustomer(request.getCustomerId());
        }
        if (request.getProductId() != null) {
            productRatePlanClient.validateProduct(request.getProductId());
        }
        if (request.getRatePlanId() != null) {
            productRatePlanClient.validateRatePlan(request.getRatePlanId());
        }
        if (request.getProductId() != null && request.getRatePlanId() != null) {
            productRatePlanClient.validateProductRatePlanLinkage(request.getProductId(), request.getRatePlanId());
        }

        // Apply updates
        mapper.updateEntityFromRequest(request, subscription);

        return enrich(repository.save(subscription));
    }

    @Override
    public SubscriptionResponse getSubscription(Long subscriptionId) {
        Long orgId = TenantContext.require();
        return repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .map(this::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }

    @Override
    public List<SubscriptionResponse> findAll() {
        Long orgId = TenantContext.require();
        return repository.findByOrganizationId(orgId)
                .stream()
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long subscriptionId) {
        Long orgId = TenantContext.require();
        Subscription s = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        repository.delete(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionsEndingBy(Instant timestamp) {
        // Query subscriptions ending by timestamp (used by Metering Service scheduler)
        List<Subscription> subscriptions = repository.findSubscriptionsEndingByTimestamp(
            timestamp,
            SubscriptionStatus.ACTIVE
        );
        
        return subscriptions.stream()
            .map(this::enrich)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionResponse advanceBillingPeriod(Long subscriptionId) {
        Long orgId = TenantContext.require();
        
        Subscription subscription = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        // Fetch rate plan to get billing frequency
        RatePlanDTO ratePlan = productRatePlanClient.getRatePlan(subscription.getRatePlanId());
        String billingFrequency = ratePlan.getBillingFrequency();
        
        if (billingFrequency == null || billingFrequency.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Rate plan does not have billing frequency configured");
        }

        // Calculate next billing period using frequency from rate plan
        Instant newStart = subscription.getCurrentBillingPeriodEnd().plusSeconds(1);
        Instant newEnd = calculatePeriodEnd(newStart, billingFrequency);
        Instant newNext = newEnd.plusSeconds(1);

        // Update subscription
        subscription.setCurrentBillingPeriodStart(newStart);
        subscription.setCurrentBillingPeriodEnd(newEnd);
        subscription.setNextBillingTimestamp(newNext);
        subscription.setLastUpdated(Instant.now());

        subscription = repository.save(subscription);
        
        return enrich(subscription);
    }

    // ========== BILLING CYCLE CALCULATION METHODS ==========

    /**
     * Calculate and set billing cycle fields based on subscription's createdOn timestamp
     * and the billing frequency from rate plan (received as String from Product Service)
     */
    private void calculateAndSetBillingCycle(Subscription subscription, String billingFrequency) {
        // Extract timestamp from createdOn (set by mapper during entity creation)
        Instant subscriptionStart = subscription.getCreatedOn();
        if (subscriptionStart == null) {
            subscriptionStart = Instant.now();
        }

        // Validate billing frequency
        if (billingFrequency == null || billingFrequency.isBlank()) {
            billingFrequency = "MONTHLY";  // Default to MONTHLY if not provided
        }

        // Calculate period end based on frequency
        Instant billingPeriodEnd = calculatePeriodEnd(subscriptionStart, billingFrequency);
        Instant nextBillingTime = billingPeriodEnd.plusSeconds(1);

        // Generate human-readable anchor info
        String anchorInfo = generateBillingAnchorInfo(subscriptionStart, billingFrequency);

        // Set all billing cycle fields
        subscription.setCurrentBillingPeriodStart(subscriptionStart);
        subscription.setCurrentBillingPeriodEnd(billingPeriodEnd);
        subscription.setNextBillingTimestamp(nextBillingTime);
        subscription.setBillingAnchorInfo(anchorInfo);
    }

    /**
     * Calculate period end timestamp for all billing frequencies
     * Uses String frequency from Product/RatePlan Service
     */
    private Instant calculatePeriodEnd(Instant start, String frequency) {
        return switch (frequency.toUpperCase()) {
            case "HOURLY" -> start.plus(1, ChronoUnit.HOURS).minusSeconds(1);
            case "DAILY" -> start.plus(1, ChronoUnit.DAYS).minusSeconds(1);
            case "WEEKLY" -> start.plus(7, ChronoUnit.DAYS).minusSeconds(1);
            case "MONTHLY" -> start.plus(30, ChronoUnit.DAYS).minusSeconds(1);  // Approximate month
            case "YEARLY" -> start.plus(365, ChronoUnit.DAYS).minusSeconds(1);  // Approximate year
            default -> start.plus(30, ChronoUnit.DAYS).minusSeconds(1);  // Default to ~MONTHLY
        };
    }

    /**
     * Generate human-readable billing schedule description
     * Uses String frequency from Product/RatePlan Service
     */
    private String generateBillingAnchorInfo(Instant start, String frequency) {
        // Convert Instant to ZonedDateTime for display formatting
        ZonedDateTime zdt = start.atZone(UTC_ZONE);
        return switch (frequency.toUpperCase()) {
            case "HOURLY" -> "Every hour at " + zdt.getMinute() + " minutes past";
            case "DAILY" -> "Daily at " + zdt.toLocalTime();
            case "WEEKLY" -> "Weekly on " + zdt.getDayOfWeek() + " at " + zdt.toLocalTime();
            case "MONTHLY" -> "Monthly on day " + zdt.getDayOfMonth() + " at " + zdt.toLocalTime();
            case "YEARLY" -> "Yearly on " + zdt.getMonth() + " " + zdt.getDayOfMonth();
            default -> "Monthly on day " + zdt.getDayOfMonth();  // Default fallback
        };
    }
}
