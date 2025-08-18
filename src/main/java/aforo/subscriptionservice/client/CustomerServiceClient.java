package aforo.subscriptionservice.client;

import aforo.subscriptionservice.client.dto.CustomerDTO;
import aforo.subscriptionservice.exception.ExternalServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(WebClient.Builder builder) {
        // ✅ Customer service base URL (your AWS URL)
        this.webClient = builder.baseUrl("http://43.206.110.213:8081/v1/api").build();
    }

    public void validateCustomer(Long customerId) {
        try {
            webClient.get()
                    .uri("/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(CustomerDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ExternalServiceException("Customer not found with ID: " + customerId, ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to validate customer with ID: " + customerId, ex);
        }
    }
}
