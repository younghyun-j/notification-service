package com.eccomerce.api.notification.productUserNotification.repository;

import com.eccomerce.api.notification.productUserNotification.domain.ProductUserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {
    @Query("SELECT pnu FROM ProductUserNotification pnu " +
            "WHERE pnu.productId = :productId " +
            "AND pnu.userRestockSubState = 'ACTIVE' " +
            "ORDER BY pnu.createdAt")
    List<ProductUserNotification> findProductNotificationActiveUserByProductId(Long productId);
}
