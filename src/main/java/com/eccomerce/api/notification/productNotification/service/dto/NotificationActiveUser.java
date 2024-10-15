package com.eccomerce.api.notification.productNotification.service.dto;

import lombok.Getter;

@Getter
public class NotificationActiveUser {
    private final Long productId;
    private final Long restockVersion;
    private final Long userId;
    private boolean isLast = false;

    public NotificationActiveUser(Long productId, Long restockVersion, Long userId) {
        this.productId = productId;
        this.restockVersion = restockVersion;
        this.userId = userId;
    }

    public void setLast() {
        this.isLast = true;
    }

}
