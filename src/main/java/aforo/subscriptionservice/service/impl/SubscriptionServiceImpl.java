package aforo.subscriptionservice.service.impl;

import aforo.subscriptionservice.client.CustomerServiceClient;
import aforo.subscriptionservice.client.ProductRatePlanClient;
import aforo.subscriptionservice.client.dto.CustomerDTO;
import aforo.subscriptionservice.client.dto.ProductDTO;
import aforo.subscriptionservice.client.dto.RatePlanDTO;
import aforo.subscriptionservice.dto.SubscriptionCreateRequest;
import aforo.subscriptionservice.dto.SubscriptionResponse;
import aforo.subscriptionservice.dto.SubscriptionUpdateRequest;
import aforo.subscriptionservice.entity.Subscription;
import aforo.subscriptionservice.mapper.SubscriptionMapper;
import aforo.subscriptionservice.repository.SubscriptionRepository;
import aforo.subscriptionservice.service.SubscriptionService;
import aforo.subscriptionservice.entity.SubscriptionStatus;
import aforo.subscriptionservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionMapper mapper;
    private final ProductRatePlanClient productRatePlanClient;
    private final CustomerServiceClient customerServiceClient;

    private SubscriptionResponse enrich(Subscription subscription) {
        SubscriptionResponse resp = mapper.toResponse(subscription);
        try {
            if (subscription.getCustomerId() != null) {
                CustomerDTO c = customerServiceClient.getCustomer(subscription.getCustomerId());
                if (c != null) resp.setCustomerName(c.getCustomerName());
            }
        } catch (Exception ignored) {}
        try {
            if (subscription.getProductId() != null) {
                ProductDTO p = productRatePlanClient.getProduct(subscription.getProductId());
                if (p != null) resp.setProductName(p.getProductName());
            }
        } catch (Exception ignored) {}
        try {
            if (subscription.getRatePlanId() != null) {
                RatePlanDTO rp = productRatePlanClient.getRatePlan(subscription.getRatePlanId());
                if (rp != null) resp.setRatePlanName(rp.getRatePlanName());
            }
        } catch (Exception ignored) {}
        return resp;
    }

    @Override
    public SubscriptionResponse confirmSubscription(Long subscriptionId) {
        Long orgId = TenantContext.require();
        Subscription subscription = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        // Validate required fields before activation
        StringBuilder missing = new StringBuilder();
        if (subscription.getCustomerId() == null) missing.append("customerId,");
        if (subscription.getProductId() == null) missing.append("productId,");
        if (subscription.getRatePlanId() == null) missing.append("ratePlanId,");
        if (subscription.getPaymentType() == null) missing.append("paymentType,");

        if (missing.length() > 0) {
            // trim trailing comma
            String msg = "Cannot confirm subscription: missing required fields [" + missing.substring(0, missing.length()-1) + "]";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setLastUpdated(LocalDateTime.now());
            subscription = repository.save(subscription);
        }
        return enrich(subscription);
    }

    @Override
    public SubscriptionResponse createSubscription(SubscriptionCreateRequest request) {
        Long orgId = TenantContext.require();
        // Validate only when provided (allow nulls end-to-end)
        if (request.getCustomerId() != null) {
            customerServiceClient.validateCustomer(request.getCustomerId());
        }
        if (request.getProductId() != null) {
            productRatePlanClient.validateProduct(request.getProductId());
        }
        if (request.getRatePlanId() != null) {
            productRatePlanClient.validateRatePlan(request.getRatePlanId());
        }
        if (request.getProductId() != null && request.getRatePlanId() != null) {
            productRatePlanClient.validateProductRatePlanLinkage(request.getProductId(), request.getRatePlanId());
        }

        Subscription subscription = mapper.toEntity(request);
        subscription.setOrganizationId(orgId);
        return enrich(repository.save(subscription));
    }

    @Override
    public SubscriptionResponse updateSubscription(Long subscriptionId, SubscriptionUpdateRequest request) {
        Long orgId = TenantContext.require();
        Subscription subscription = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        // Validate present fields only (PATCH semantics)
        if (request.getCustomerId() != null) {
            customerServiceClient.validateCustomer(request.getCustomerId());
        }
        if (request.getProductId() != null) {
            productRatePlanClient.validateProduct(request.getProductId());
        }
        if (request.getRatePlanId() != null) {
            productRatePlanClient.validateRatePlan(request.getRatePlanId());
        }
        if (request.getProductId() != null && request.getRatePlanId() != null) {
            productRatePlanClient.validateProductRatePlanLinkage(request.getProductId(), request.getRatePlanId());
        }

        // Apply updates
        mapper.updateEntityFromRequest(request, subscription);

        return enrich(repository.save(subscription));
    }

    @Override
    public SubscriptionResponse getSubscription(Long subscriptionId) {
        Long orgId = TenantContext.require();
        return repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .map(this::enrich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }

    @Override
    public List<SubscriptionResponse> findAll() {
        Long orgId = TenantContext.require();
        return repository.findByOrganizationId(orgId)
                .stream()
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long subscriptionId) {
        Long orgId = TenantContext.require();
        Subscription s = repository.findBySubscriptionIdAndOrganizationId(subscriptionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        repository.delete(s);
    }
}
