package aforo.subscriptionservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrganizationClient {

    private final WebClient webClient;

    public OrganizationClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    public void validateOrganization(UUID id) {
        try {
            webClient.get()
                    .uri("/api/organizations/{id}", id.toString())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            throw new aforo.subscriptionservice.exception.ExternalServiceException("Organization validation failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        }
    }

    public void validateDivision(UUID id) {
        try {
            webClient.get()
                    .uri("/api/divisions/{id}", id != null ? id.toString() : null)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            throw new aforo.subscriptionservice.exception.ExternalServiceException("Division validation failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        }
    }

    public void validateCustomer(UUID id) {
        try {
            webClient.get()
                    .uri("/api/customers/{id}", id.toString())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            throw new aforo.subscriptionservice.exception.ExternalServiceException("Customer validation failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        }
    }
}
