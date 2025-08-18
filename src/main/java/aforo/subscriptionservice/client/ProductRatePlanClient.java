package aforo.subscriptionservice.client;

import aforo.subscriptionservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductRatePlanClient {

    @Qualifier("productWebClient")
    private final WebClient webClient;

    public void validateProduct(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }

        webClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.statusCode() == HttpStatus.NOT_FOUND
                                ? Mono.error(new IllegalArgumentException("Invalid product ID: " + id))
                                : resp.bodyToMono(String.class)
                                      .defaultIfEmpty("Bad request to product service")
                                      .flatMap(msg -> Mono.error(new IllegalArgumentException(msg))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new ExternalServiceException("Product service is currently unavailable", null)))
                .toBodilessEntity()
                .onErrorMap(WebClientRequestException.class,
                        ex -> new ExternalServiceException("Product service is currently unavailable", ex))
                .block();
    }

    public void validateRatePlan(Integer ratePlanId, Integer productId) {
        validateProduct(productId);
        if (ratePlanId == null || ratePlanId <= 0) {
            throw new IllegalArgumentException("Invalid rate plan ID: " + ratePlanId);
        }
    
        RatePlanResponse rp = webClient.get()
            .uri("/api/rateplans/{id}", ratePlanId)
            .retrieve()
            // 404 -> 400
            .onStatus(HttpStatusCode::is4xxClientError, r ->
                r.statusCode() == HttpStatus.NOT_FOUND
                    ? Mono.error(new IllegalArgumentException("Invalid rate plan ID: " + ratePlanId))
                    : r.bodyToMono(String.class).flatMap(msg ->
                        Mono.error(new IllegalArgumentException(msg == null ? "Bad request to product service" : msg)))
            )
            // 5xx: if body says "not found", treat as 400; else 503
            .onStatus(HttpStatusCode::is5xxServerError, r ->
                r.bodyToMono(String.class).defaultIfEmpty("")
                 .flatMap(body -> containsNotFound(body)
                     ? Mono.error(new IllegalArgumentException("Invalid rate plan ID: " + ratePlanId))
                     : Mono.error(new ExternalServiceException("Product service is currently unavailable", null)))
            )
            .bodyToMono(RatePlanResponse.class)
            .onErrorMap(WebClientRequestException.class,
                ex -> new ExternalServiceException("Product service is currently unavailable", ex))
            .block();
    
        if (rp != null && !Objects.equals(rp.getProductId(), productId)) {
            throw new IllegalArgumentException("Rate plan " + ratePlanId + " does not belong to product " + productId);
        }
    }
    
    private static boolean containsNotFound(String body) {
        if (body == null) return false;
        String s = body.toLowerCase();
        return s.contains("not found") || s.contains("does not exist") || s.contains("no such");
    }
    

    private static class RatePlanResponse {
        private Integer ratePlanId;
        private Integer productId;
        private String status;
        public Integer getRatePlanId() { return ratePlanId; }
        public Integer getProductId() { return productId; }
        public String getStatus() { return status; }
        public void setRatePlanId(Integer ratePlanId) { this.ratePlanId = ratePlanId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        public void setStatus(String status) { this.status = status; }
    }
}
