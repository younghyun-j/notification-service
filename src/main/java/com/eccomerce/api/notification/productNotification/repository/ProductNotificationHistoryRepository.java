package com.eccomerce.api.notification.productNotification.repository;

import com.eccomerce.api.notification.productNotification.domain.ProductNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory, Long> {
    Optional<ProductNotificationHistory> findByProductIdAndRestockVersion(Long productId, Long restockVersion);
}
