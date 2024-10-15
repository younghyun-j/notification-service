package com.eccomerce.api.notification.productNotification.service;

import com.eccomerce.api.notification.productNotification.domain.ProductNotificationHistory;
import com.eccomerce.api.notification.productNotification.domain.RestockNotificationState;
import com.eccomerce.api.notification.productNotification.repository.ProductNotificationHistoryRepository;
import com.eccomerce.api.notification.productNotification.service.dto.NotificationActiveUser;
import com.eccomerce.api.notification.productUserNotification.domain.ProductUserNotification;
import com.eccomerce.api.notification.productUserNotification.domain.ProductUserNotificationHistory;
import com.eccomerce.api.notification.productUserNotification.repository.ProductUserNotificationHistoryRepository;
import com.eccomerce.api.notification.productUserNotification.repository.ProductUserNotificationRepository;
import com.eccomerce.api.product.domain.Product;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class NotificationManager {

    private final BlockingQueue<NotificationActiveUser> notificationUsers;
    private final Set<Long> activeProducts;
    private final NotificationSender notificationSender;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository;


    public NotificationManager(BlockingQueue<NotificationActiveUser> notificationUsers,
                               Set<Long> notificationActiveProducts,
                               NotificationSender notificationSender,
                               ProductNotificationHistoryRepository productNotificationHistoryRepository,
                               ProductUserNotificationRepository productUserNotificationRepository,
                               ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository) {
        this.notificationUsers = notificationUsers;
        this.activeProducts = notificationActiveProducts;
        this.notificationSender = notificationSender;
        this.productNotificationHistoryRepository = productNotificationHistoryRepository;
        this.productUserNotificationRepository = productUserNotificationRepository;
        this.productUserNotificationHistoryRepository = productUserNotificationHistoryRepository;
    }

    @PostConstruct
    public void init() {
        new Thread(notificationSender).start();
    }

    public void startNotificationsForRestockProduct(Product product) {
        /**
         * 상품이 재입고 되었을 때, 재입고 알림을 설정한 유저들에게 알림 메시지를 전송
         */
        createRestockHistory(product);
        activeProducts.add(product.getId());
        setNotificationActiveUsers(product);
    }

    public void stopNotificationsForProduct(Product product) {
        if(!activeProducts.contains(product.getId())) return;
        /**
         * 재입고 알림을 보내던 중 재고가 모두 없어진다면 알림 보내는 것을 중단
         */
        activeProducts.remove(product.getId());
        updateStateToCancelledBySoldOut(product.getId(), product.getRestockVersion());

    }

    private void createRestockHistory(Product product) {
        ProductNotificationHistory history = new ProductNotificationHistory(product.getId(), product.getRestockVersion(), RestockNotificationState.IN_PROGRESS);
        productNotificationHistoryRepository.save(history);
    }

    private void setNotificationActiveUsers(Product product) {
        List<ProductUserNotification> getNotificationActiveUsers = productUserNotificationRepository.findProductNotificationActiveUserByProductId(product.getId());

        long productId = product.getId();
        long restockVersion = product.getRestockVersion();
        long lastUserId = getNotificationActiveUsers.getLast().getUserId();
        getNotificationActiveUsers.stream()
                .map(user -> new NotificationActiveUser(productId, restockVersion, user.getUserId()))
                .peek(activeUser -> {
                    if (activeUser.getUserId() == lastUserId) {
                        activeUser.setLast();
                    }
                })
                .forEach(notificationUsers::offer);
    }

    private void updateStateToCancelledBySoldOut(Long productId, Long restockVersion) {
        /**
         * lastUser가 null이라면 해당 상품은 한번도 알림 발송이 되지 않은 것
         */
        ProductUserNotificationHistory lastUser = productUserNotificationHistoryRepository
                .findTopByProductIdAndRestockVersionOrderByCreatedAtDesc(productId, restockVersion) // Version
                .orElse(null);

        productNotificationHistoryRepository.findByProductIdAndRestockVersion(productId, restockVersion)
                .ifPresent(history -> {
                    history.updateNotificationState(RestockNotificationState.CANCELLED_BY_SOLD_OUT);
                    if(lastUser != null) {
                        history.updateLastNotificationUserId(lastUser.getUserId());
                    }
                    productNotificationHistoryRepository.save(history);
                });
    }

}
