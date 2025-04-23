package com.satya.demo.dto;

import lombok.Data;

@Data
public class ProductRequest {
        private Long productId;
        private String productName;
        private double productPrice;
}
