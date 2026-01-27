package aforo.subscriptionservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import aforo.subscriptionservice.client.dto.RatePlanDTO;
import aforo.subscriptionservice.client.dto.ProductDTO;
import aforo.subscriptionservice.exception.ExternalServiceException;

@Component
public class ProductRatePlanClient {

    private final WebClient webClient;

    public ProductRatePlanClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://product.dev.aforo.space:8080").build();
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

    public void validateProduct(Long id) {
        webClient.get()
                .uri("/api/products/{id}", id)
                .header("Authorization", getBearerToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void validateRatePlan(Long id) {
        webClient.get()
                .uri("/api/rateplans/{rateplanId}", id)
                .header("Authorization", getBearerToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void validateProductRatePlanLinkage(Long productId, Long ratePlanId) {
        try {
            RatePlanDTO ratePlan = webClient.get()
                    .uri("/api/rateplans/{ratePlanId}", ratePlanId)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToMono(RatePlanDTO.class)
                    .block();

            if (ratePlan == null || !ratePlan.getProductId().equals(productId)) {
                throw new ExternalServiceException(
                    String.format("RatePlan %d does not belong to Product %d", ratePlanId, productId), null
                );
            }
        } catch (WebClientResponseException.NotFound ex) {
            throw new ExternalServiceException("RatePlan not found with ID: " + ratePlanId, ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to validate product-rateplan linkage", ex);
        }
    }

    public ProductDTO getProduct(Long id) {
        try {
            return webClient.get()
                    .uri("/api/products/{id}", id)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ExternalServiceException("Product not found with ID: " + id, ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to fetch product with ID: " + id, ex);
        }
    }

    public RatePlanDTO getRatePlan(Long id) {
        try {
            return webClient.get()
                    .uri("/api/rateplans/{ratePlanId}", id)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToMono(RatePlanDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ExternalServiceException("RatePlan not found with ID: " + id, ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to fetch rate plan with ID: " + id, ex);
        }
    }
}
