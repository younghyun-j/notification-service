package com.eccomerce.api.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Table(name = "Product")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long restockVersion;
    private int quantity;

    public Product(Long restockVersion, int quantity) {
        this.restockVersion = restockVersion;
        this.quantity = quantity;
    }

    public void updateRestock() {
        this.restockVersion++;
    }

    public void updateQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void minusQuantity(Integer quantity) {
        if(isSoldOut()) return;
        this.quantity -= quantity;
    }

    public boolean isSoldOut() {
        return this.quantity <= 0;
    }
}
