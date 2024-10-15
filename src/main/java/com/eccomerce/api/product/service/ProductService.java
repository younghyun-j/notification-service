package com.eccomerce.api.product.service;

import com.eccomerce.api.notification.productNotification.service.NotificationManager;
import com.eccomerce.api.product.domain.Product;
import com.eccomerce.api.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationManager notificationManager;

    public void notifyRestock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.updateRestock();
        product.updateQuantity(quantity);
        notificationManager.startNotificationsForRestockProduct(product);
    }

    @Transactional
    public void sell(Long productId, int quantity) {
        Product product = getProductById(productId);
        if (product.getQuantity() - quantity < 0) {
            throw new IllegalArgumentException("Quantity less than or equal to zero");
        }
        product.minusQuantity(quantity);

        if (product.isSoldOut()) {
            notificationManager.stopNotificationsForProduct(product);
        }
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}
