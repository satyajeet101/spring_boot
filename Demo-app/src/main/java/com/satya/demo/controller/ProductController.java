package com.satya.demo.controller;

import com.satya.demo.dto.ProductRequest;
import com.satya.demo.dto.ProductResponse;
import com.satya.demo.entity.ProductEntity;
import com.satya.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("")
    public List<ProductResponse> getProduct() {
        return productService.getProduct();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("")
    public ResponseEntity<ProductEntity> getProduct(@RequestBody ProductRequest request) {
        ProductEntity productEntity = ProductEntity.builder()
                .productName(request.getProductName())
                .productPrice(request.getProductPrice())
                .build();
        return ResponseEntity.ok(productService.addProduct(productEntity));
    }

    @PutMapping("")
    public String updateProduct(@PathVariable int id, @RequestBody ProductEntity product) {
        return productService.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    public String removeProduct(@PathVariable Long id) {
        return productService.removeProduct(id);
    }
}