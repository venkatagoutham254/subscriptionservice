package aforo.subscriptionservice.client;

import aforo.subscriptionservice.client.dto.CustomerDTO;
import aforo.subscriptionservice.exception.ExternalServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(WebClient.Builder builder) {
        // ✅ Customer service base URL (updated AWS URL)
        this.webClient = builder.baseUrl("http://44.201.19.187:8081/v1/api").build();
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

    public void validateCustomer(Long customerId) {
        try {
            webClient.get()
                    .uri("/customers/{id}", customerId)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToMono(CustomerDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ExternalServiceException("Customer not found with ID: " + customerId, ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to validate customer with ID: " + customerId, ex);
        }
    }

    public CustomerDTO getCustomer(Long customerId) {
        try {
            return webClient.get()
                    .uri("/customers/{id}", customerId)
                    .header("Authorization", getBearerToken())
                    .retrieve()
                    .bodyToMono(CustomerDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ExternalServiceException("Customer not found with ID: " + customerId, ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to fetch customer with ID: " + customerId, ex);
        }
    }
}
