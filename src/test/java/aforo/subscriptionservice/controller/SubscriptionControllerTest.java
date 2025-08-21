package aforo.subscriptionservice.controller;

import aforo.subscriptionservice.controller.SubscriptionController;
import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
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
    void confirmSubscription_returns200() throws Exception {
        Long id = 5L;
        when(subscriptionService.confirmSubscription(id)).thenReturn(Mockito.mock(SubscriptionResponse.class));

        mockMvc.perform(post("/api/subscriptions/{id}/confirm", id))
               .andExpect(status().isOk());

        verify(subscriptionService).confirmSubscription(id);
    }

    @Test
    void getAll_returns200() throws Exception {
        when(subscriptionService.findAll()).thenReturn(List.of(Mockito.mock(SubscriptionResponse.class)));

        mockMvc.perform(get("/api/subscriptions"))
               .andExpect(status().isOk());

        verify(subscriptionService).findAll();
    }

    // If your controller has POST /api/subscriptions for create:
    @Test
    void createSubscription_returns200() throws Exception {
        // NOTE: adjust JSON fields to your actual SubscriptionCreateRequest
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
               .andExpect(status().isOk()); // use isCreated() if your controller returns 201

        verify(subscriptionService).createSubscription(any(SubscriptionCreateRequest.class));
    }

    // If your controller has PATCH /api/subscriptions/{id} for update:
    @Test
    void updateSubscription_returns200() throws Exception {
        Long id = 10L;
        // NOTE: adjust JSON fields to your actual SubscriptionUpdateRequest
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

    // Example of a validation failure: send empty body to create (expects 400 if @Valid fails)
    @Test
    void createSubscription_invalidBody_returns400() throws Exception {
        // Will return 400 only if your DTO has Bean Validation annotations and controller method has @Valid
        when(subscriptionService.createSubscription(any())).thenReturn(Mockito.mock(SubscriptionResponse.class));

        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
               .andExpect(status().isBadRequest());
    }
}