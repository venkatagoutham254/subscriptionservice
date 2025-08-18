package aforo.subscriptionservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import aforo.subscriptionservice.client.dto.RatePlanDTO;
import aforo.subscriptionservice.exception.ExternalServiceException;

@Component
public class ProductRatePlanClient {

    private final WebClient webClient;

    public ProductRatePlanClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://54.238.204.246:8080").build();
    }

    public void validateProduct(Long id) {
        webClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void validateRatePlan(Long id) {
        webClient.get()
                .uri("/api/rateplans/{rateplanId}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }


public void validateProductRatePlanLinkage(Long productId, Long ratePlanId) {
    try {
        RatePlanDTO ratePlan = webClient.get()
                .uri("/api/rateplans/{ratePlanId}", ratePlanId)
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


}
