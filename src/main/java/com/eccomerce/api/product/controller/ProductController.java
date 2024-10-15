package com.eccomerce.api.product.controller;

import com.eccomerce.api.product.dto.CreateRestockRequest;
import com.eccomerce.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{productId}/notifications/re-stock")
    public ResponseEntity<Void> productRestockNotification(@PathVariable Long productId, @RequestBody CreateRestockRequest request) {
        productService.notifyRestock(productId, request.quantity());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/sell")
    public ResponseEntity<Void> sell(@PathVariable Long productId, @RequestParam int quantity) {
        productService.sell(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
