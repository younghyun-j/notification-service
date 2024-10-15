package com.eccomerce.api.notification.productUserNotification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "ProductUserNotification")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private UserRestockSubState userRestockSubState;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductUserNotification(Long productId, Long userId, UserRestockSubState userRestockSubState) {
        this.productId = productId;
        this.userId = userId;
        this.userRestockSubState = userRestockSubState;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
