package com.eccomerce.api.notification.productNotification.service;

import com.eccomerce.api.notification.productNotification.domain.RestockNotificationState;
import com.eccomerce.api.notification.productNotification.service.dto.NotificationActiveUser;
import com.eccomerce.api.notification.productNotification.repository.ProductNotificationHistoryRepository;
import com.eccomerce.api.notification.productUserNotification.domain.ProductUserNotificationHistory;
import com.eccomerce.api.notification.productUserNotification.repository.ProductUserNotificationHistoryRepository;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class NotificationSender implements Runnable {

    private final Bucket bucket;
    private final BlockingQueue<NotificationActiveUser> notificationUsers;
    private final Set<Long> activeProducts;
    private final ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;

    public NotificationSender(Bucket bucket,
                              BlockingQueue<NotificationActiveUser> notificationUsers,
                              Set<Long> activeProducts,
                              ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository,
                              ProductNotificationHistoryRepository productNotificationHistoryRepository) {
        this.bucket = bucket;
        this.notificationUsers = notificationUsers;
        this.activeProducts = activeProducts;
        this.productUserNotificationHistoryRepository = productUserNotificationHistoryRepository;
        this.productNotificationHistoryRepository = productNotificationHistoryRepository;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(!bucket.tryConsume(1)) continue;
                NotificationActiveUser activeUser = notificationUsers.take();
                if(isSoldOut(activeUser.getProductId())) continue;

                /**
                 * 회차별 재입고 알림을 받은 유저 목록을 저장
                 */
                ProductUserNotificationHistory history = new ProductUserNotificationHistory(
                        activeUser.getProductId(),
                        activeUser.getUserId(),
                        activeUser.getRestockVersion());
                productUserNotificationHistoryRepository.save(history);

                /**
                 * 마지막 발송 유저 아이디 저장
                 */
                if (activeUser.isLast()) {
                    productNotificationHistoryRepository.findByProductIdAndRestockVersion(activeUser.getProductId(), activeUser.getRestockVersion())
                            .ifPresent(getHistory -> {
                                getHistory.updateNotificationState(RestockNotificationState.COMPLETED);
                                getHistory.updateLastNotificationUserId(activeUser.getUserId());
                                productNotificationHistoryRepository.save(getHistory);
                    });
                    activeProducts.remove(activeUser.getProductId());
                }
            }
        } catch (InterruptedException e) {
            log.info("InterruptedException");
        }
    }

    private boolean isSoldOut(Long productId) {
        return !activeProducts.contains(productId);
    }
}
