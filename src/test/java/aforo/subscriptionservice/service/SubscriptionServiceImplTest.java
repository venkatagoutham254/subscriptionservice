package aforo.subscriptionservice.service;

import aforo.subscriptionservice.client.CustomerServiceClient;
import aforo.subscriptionservice.client.ProductRatePlanClient;
import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.entity.PaymentType;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import aforo.subscriptionservice.mapper.SubscriptionMapper;
import aforo.subscriptionservice.repository.SubscriptionRepository;
import aforo.subscriptionservice.service.impl.SubscriptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for SubscriptionServiceImpl.
 * Covers happy paths + 404 cases + new delete() method.
 */
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock SubscriptionRepository repository;
    @Mock SubscriptionMapper mapper;
    @Mock CustomerServiceClient customerClient;
    @Mock ProductRatePlanClient productRatePlanClient;

    @InjectMocks SubscriptionServiceImpl service;

    @Test
    void getSubscription_found_returnsResponse() {
        Subscription entity = new Subscription();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        SubscriptionResponse resp = mock(SubscriptionResponse.class);
        when(mapper.toResponse(entity)).thenReturn(resp);

        SubscriptionResponse out = service.getSubscription(1L);

        assertSame(resp, out);
        verify(repository).findById(1L);
        verify(mapper).toResponse(entity);
    }

    @Test
    void getSubscription_notFound_throws404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> service.getSubscription(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void findAll_mapsAll() {
        when(repository.findAll()).thenReturn(List.of(new Subscription(), new Subscription()));
        when(mapper.toResponse(any(Subscription.class))).thenReturn(mock(SubscriptionResponse.class));

        List<SubscriptionResponse> out = service.findAll();

        assertEquals(2, out.size());
        verify(repository).findAll();
        verify(mapper, times(2)).toResponse(any(Subscription.class));
    }

    @Test
    void confirmSubscription_setsActive_andSaves() {
        Long id = 7L;
        Subscription entity = new Subscription();
        entity.setStatus(SubscriptionStatus.DRAFT);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Subscription.class))).thenReturn(mock(SubscriptionResponse.class));

        SubscriptionResponse out = service.confirmSubscription(id);

        assertNotNull(out);
        assertEquals(SubscriptionStatus.ACTIVE, entity.getStatus());
        verify(repository).save(entity);
    }

    @Test
    void confirmSubscription_notFound_throws404() {
        Long id = 404L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> service.confirmSubscription(id));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void createSubscription_valid_saves_andMaps() {
        // Build a non-null request
        SubscriptionCreateRequest req = new SubscriptionCreateRequest();
        req.setCustomerId(1L);
        req.setProductId(2L);
        req.setRatePlanId(3L);
        req.setPaymentType(PaymentType.PREPAID);
        req.setAdminNotes("note");

        // Stub external validators (void methods)
        doNothing().when(customerClient).validateCustomer(eq(1L));
        doNothing().when(productRatePlanClient).validateProduct(eq(2L));
        doNothing().when(productRatePlanClient).validateProductRatePlanLinkage(eq(2L), eq(3L));

        // Mapper & repo stubs
        Subscription entity = new Subscription();
        when(mapper.toEntity(req)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(mock(SubscriptionResponse.class));

        // Call
        SubscriptionResponse out = service.createSubscription(req);

        // Assert + verify
        assertNotNull(out);
        verify(customerClient).validateCustomer(1L);
        verify(productRatePlanClient).validateProduct(2L);
        verify(productRatePlanClient).validateProductRatePlanLinkage(2L, 3L);
        verify(repository).save(entity);
    }

    @Test
    void updateSubscription_maps_andSaves() {
        Long id = 100L;
        SubscriptionUpdateRequest req = new SubscriptionUpdateRequest();

        Subscription existing = new Subscription();
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(mock(SubscriptionResponse.class));

        SubscriptionResponse out = service.updateSubscription(id, req);

        assertNotNull(out);
        // Adjust params order to match your mapper signature
        verify(mapper).updateEntityFromRequest(eq(req), eq(existing));
        verify(repository).save(existing);
        verify(mapper).toResponse(existing);
    }

    @Test
    void delete_exists_deletes() {
        Long id = 15L;
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_notFound_throws404_andDoesNotDelete() {
        Long id = 16L;
        when(repository.existsById(id)).thenReturn(false);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> service.delete(id));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(repository, never()).deleteById(anyLong());
    }
}
