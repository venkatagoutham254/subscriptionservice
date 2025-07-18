package aforo.subscriptionservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductRatePlanClient {

    private final WebClient webClient;

    public ProductRatePlanClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://13.230.194.245:8080").build();
    }

    public void validateProduct(Integer id) {
        webClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void validateRatePlan(Integer id) {
        webClient.get()
                .uri("/api/rateplans/{rateplanId}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
