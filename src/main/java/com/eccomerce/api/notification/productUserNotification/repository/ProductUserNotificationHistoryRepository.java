package com.eccomerce.api.notification.productUserNotification.repository;

import com.eccomerce.api.notification.productUserNotification.domain.ProductUserNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductUserNotificationHistoryRepository extends JpaRepository<ProductUserNotificationHistory, Long> {
    Optional<ProductUserNotificationHistory> findTopByProductIdAndRestockVersionOrderByCreatedAtDesc(Long id, Long restockVersion);
}
