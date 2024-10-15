package com.eccomerce.api.notification.productNotification.service.config;

import com.eccomerce.api.notification.productNotification.service.NotificationManager;
import com.eccomerce.api.notification.productNotification.service.NotificationSender;
import com.eccomerce.api.notification.productNotification.repository.ProductNotificationHistoryRepository;
import com.eccomerce.api.notification.productNotification.service.dto.NotificationActiveUser;
import com.eccomerce.api.notification.productUserNotification.repository.ProductUserNotificationHistoryRepository;
import com.eccomerce.api.notification.productUserNotification.repository.ProductUserNotificationRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@AllArgsConstructor
public class NotificationConfig {

    /**
     * 알림 메시지 전송 제한: 1초당 최대 500개
     */
    private static final Duration REFILL_INTERVAL = Duration.ofSeconds(1);
    private static final int NOTIFICATIONS_PER_SECOND = 500;

    private final BlockingQueue<NotificationActiveUser> notificationUsers = new LinkedBlockingQueue<>();
    private final Set<Long> notificationActiveProducts = ConcurrentHashMap.newKeySet();
    private final ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductUserNotificationRepository productUserNotificationRepository;


    @Bean
    public NotificationManager notificationManager() {
        return new NotificationManager(
                notificationUsers,
                notificationActiveProducts,
                createNotificationSender(),
                productNotificationHistoryRepository,
                productUserNotificationRepository,
                productUserNotificationHistoryRepository
        );
    }

    private Bucket createNotificationBucket(){
        Refill refill = Refill.intervally(NOTIFICATIONS_PER_SECOND, REFILL_INTERVAL);
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private NotificationSender createNotificationSender() {
        return new NotificationSender(
                createNotificationBucket(),
                notificationUsers,
                notificationActiveProducts,
                productUserNotificationHistoryRepository,
                productNotificationHistoryRepository);
    }

}
