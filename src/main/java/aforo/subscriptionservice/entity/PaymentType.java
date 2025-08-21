package aforo.subscriptionservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentType {
    PREPAID,
    POSTPAID;

    @JsonCreator
    public static PaymentType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return PaymentType.valueOf(value.toUpperCase());
    }
}

