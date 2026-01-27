package aforo.subscriptionservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import aforo.subscriptionservice.client.dto.BillableMetricDTO;
import aforo.subscriptionservice.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class BillableMetricsClient {

    private static final Logger log = LoggerFactory.getLogger(BillableMetricsClient.class);
    private final WebClient webClient;

    public BillableMetricsClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://usage.dev.aforo.space:8081").build();
    }

    private String getBearerToken() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (principal instanceof Jwt jwt) {
            return "Bearer " + jwt.getTokenValue();
        }
        return null;
    }

    public List<BillableMetricDTO> getBillableMetricsByProduct(Long productId) {
        try {
            log.info("Fetching billable metrics for product ID: {}", productId);
            List<BillableMetricDTO> metrics = webClient.get()
                    .uri("/api/billable-metrics/by-product?productId={productId}", productId)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToFlux(BillableMetricDTO.class)
                    .collectList()
                    .block();
            log.info("Received {} billable metrics for product ID: {}", metrics != null ? metrics.size() : 0, productId);
            if (metrics != null && !metrics.isEmpty()) {
                log.info("First metric - UID: {}, Condition: {}", metrics.get(0).getBillableUid(), metrics.get(0).getUsageCondition());
            }
            return metrics;
        } catch (WebClientResponseException.NotFound ex) {
            log.warn("No billable metrics found for product ID: {}", productId);
            return List.of();
        } catch (Exception ex) {
            log.error("Failed to fetch billable metrics for product ID: {}", productId, ex);
            throw new ExternalServiceException("Failed to fetch billable metrics for product ID: " + productId, ex);
        }
    }

    public BillableMetricDTO getBillableMetric(Long billableMetricId) {
        try {
            return webClient.get()
                    .uri("/api/billable-metrics/{id}", billableMetricId)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToMono(BillableMetricDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            return null;
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to fetch billable metric with ID: " + billableMetricId, ex);
        }
    }
}
