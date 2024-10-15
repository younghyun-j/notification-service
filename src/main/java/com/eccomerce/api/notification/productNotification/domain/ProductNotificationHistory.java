package com.eccomerce.api.notification.productNotification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "ProductNotificationHistory")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductNotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long restockVersion;
    @Enumerated(EnumType.STRING)
    private RestockNotificationState restockNotificationState;
    private Long lastNotificationUserId;

    public ProductNotificationHistory(Long productId,
                                      Long restockVersion,
                                      RestockNotificationState restockNotificationState) {
        this.productId = productId;
        this.restockVersion = restockVersion;
        this.restockNotificationState = restockNotificationState;
    }

    public void updateNotificationState(RestockNotificationState state) {
        this.restockNotificationState = state;
    }

    public void updateLastNotificationUserId(Long lastNotificationUserId) {
        this.lastNotificationUserId = lastNotificationUserId;
    }
}
