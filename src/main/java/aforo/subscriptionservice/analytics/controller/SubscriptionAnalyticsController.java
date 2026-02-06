package aforo.subscriptionservice.analytics.controller;

import aforo.subscriptionservice.analytics.dto.*;
import aforo.subscriptionservice.analytics.service.SubscriptionAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions/analytics")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionAnalyticsController {

    private final SubscriptionAnalyticsService analyticsService;

    /**
     * API 1: Get Churn Rate
     * GET /api/subscriptions/analytics/churn-rate
     */
    @GetMapping("/churn-rate")
    public ResponseEntity<ChurnRateResponse> getChurnRate(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("GET /churn-rate - organizationId: {}", organizationId);
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        ChurnRateResponse response = analyticsService.getChurnRate(organizationId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * API 2: Get MRR by Customer
     * GET /api/subscriptions/analytics/mrr-by-customer
     */
    @GetMapping("/mrr-by-customer")
    public ResponseEntity<CustomerMrrResponse> getMrrByCustomer(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestParam(required = false) List<Long> customerIds) {
        
        log.info("GET /mrr-by-customer - organizationId: {}", organizationId);
        
        CustomerMrrResponse response = analyticsService.getMrrByCustomer(organizationId, customerIds);
        return ResponseEntity.ok(response);
    }

    /**
     * API 3: Get Customer Subscription Health
     * GET /api/subscriptions/analytics/customer-health/{customerId}
     */
    @GetMapping("/customer-health/{customerId}")
    public ResponseEntity<SubscriptionHealthResponse> getCustomerHealth(
            @PathVariable Long customerId,
            @RequestHeader("X-Organization-Id") Long organizationId) {
        
        log.info("GET /customer-health/{} - organizationId: {}", customerId, organizationId);
        
        SubscriptionHealthResponse response = analyticsService.getCustomerHealth(customerId, organizationId);
        return ResponseEntity.ok(response);
    }

    /**
     * API 4: Get Active Subscription Count
     * GET /api/subscriptions/analytics/active-count
     */
    @GetMapping("/active-count")
    public ResponseEntity<Long> getActiveSubscriptionCount(
            @RequestHeader("X-Organization-Id") Long organizationId) {
        
        log.info("GET /active-count - organizationId: {}", organizationId);
        
        Long count = analyticsService.getActiveSubscriptionCount(organizationId);
        return ResponseEntity.ok(count);
    }
}
