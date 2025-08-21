package aforo.subscriptionservice.controller;

import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice tests for SubscriptionController.
 * Includes DELETE and 404 mapping via GlobalExceptionHandler.
 */
@WebMvcTest(SubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(aforo.subscriptionservice.exception.GlobalExceptionHandler.class) // <-- adjust package if different
class SubscriptionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean SubscriptionService subscriptionService;

    @Test
    void getSubscription_returns200() throws Exception {
        Long id = 42L;
        when(subscriptionService.getSubscription(id)).thenReturn(Mockito.mock(SubscriptionResponse.class));

        mockMvc.perform(get("/api/subscriptions/{id}", id))
               .andExpect(status().isOk());

        verify(subscriptionService).getSubscription(id);
    }

    @Test
    void getSubscription_notFound_returns404() throws Exception {
        Long id = 404L;
        when(subscriptionService.getSubscription(id))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        mockMvc.perform(get("/api/subscriptions/{id}", id))
               .andExpect(status().isNotFound())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.error").value("Subscription not found"));
    }

    @Test
    void confirmSubscription_returns200() throws Exception {
        Long id = 5L;
        when(subscriptionService.confirmSubscription(id)).thenReturn(Mockito.mock(SubscriptionResponse.class));

        mockMvc.perform(post("/api/subscriptions/{id}/confirm", id))
               .andExpect(status().isOk());

        verify(subscriptionService).confirmSubscription(id);
    }

    @Test
    void confirmSubscription_notFound_returns404() throws Exception {
        Long id = 404L;
        when(subscriptionService.confirmSubscription(id))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        mockMvc.perform(post("/api/subscriptions/{id}/confirm", id))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Subscription not found"));
    }

    @Test
    void getAll_returns200() throws Exception {
        when(subscriptionService.findAll()).thenReturn(List.of(Mockito.mock(SubscriptionResponse.class)));

        mockMvc.perform(get("/api/subscriptions"))
               .andExpect(status().isOk());

        verify(subscriptionService).findAll();
    }

    @Test
    void createSubscription_returns200() throws Exception {
        String json = """
        {
          "customerId": 1,
          "productId": 2,
          "ratePlanId": 3,
          "paymentType": "PREPAID",
          "adminNotes": "note"
        }
        """;

        when(subscriptionService.createSubscription(any(SubscriptionCreateRequest.class)))
                .thenReturn(Mockito.mock(SubscriptionResponse.class));

        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
               .andExpect(status().isOk()); // change to isCreated() if your controller returns 201

        verify(subscriptionService).createSubscription(any(SubscriptionCreateRequest.class));
    }

    @Test
    void updateSubscription_returns200() throws Exception {
        Long id = 10L;
        String json = """
        {
          "paymentType": "POSTPAID",
          "adminNotes": "updated note"
        }
        """;

        when(subscriptionService.updateSubscription(eq(id), any(SubscriptionUpdateRequest.class)))
                .thenReturn(Mockito.mock(SubscriptionResponse.class));

        mockMvc.perform(patch("/api/subscriptions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
               .andExpect(status().isOk());

        verify(subscriptionService).updateSubscription(eq(id), any(SubscriptionUpdateRequest.class));
    }

    @Test
    void deleteSubscription_returns204() throws Exception {
        Long id = 9L;
        doNothing().when(subscriptionService).delete(id);

        mockMvc.perform(delete("/api/subscriptions/{id}", id))
               .andExpect(status().isNoContent());

        verify(subscriptionService).delete(id);
    }

    @Test
    void deleteSubscription_notFound_returns404() throws Exception {
        Long id = 123L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"))
                .when(subscriptionService).delete(id);

        mockMvc.perform(delete("/api/subscriptions/{id}", id))
               .andExpect(status().isNotFound())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.error").value("Subscription not found"));
    }

    @Test
    void createSubscription_invalidBody_returns400() throws Exception {
        // Validation should fail before the service is called
        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
               .andExpect(status().isBadRequest());

        verifyNoInteractions(subscriptionService);
    }
}
