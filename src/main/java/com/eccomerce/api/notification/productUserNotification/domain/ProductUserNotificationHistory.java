package com.eccomerce.api.notification.productUserNotification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "ProductUserNotificationHistory")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long userId;
    private Long restockVersion;
    private LocalDateTime createdAt;

    public ProductUserNotificationHistory(Long productId, Long userId, Long restockVersion) {
        this.productId = productId;
        this.userId = userId;
        this.restockVersion = restockVersion;
        this.createdAt = LocalDateTime.now();
    }
}
