package aforo.subscriptionservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SubscriptionStatus {
    DRAFT,
    ACTIVE,
    INACTIVE;

    @JsonCreator
    public static SubscriptionStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        return SubscriptionStatus.valueOf(value.toUpperCase());
    }
}
