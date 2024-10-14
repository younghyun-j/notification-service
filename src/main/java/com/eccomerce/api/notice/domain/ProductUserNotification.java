package com.eccomerce.api.notice.domain;

import com.eccomerce.api.product.domain.Product;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;
    private Long userId;
    private UserRestockSubState userRestockSubState;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
