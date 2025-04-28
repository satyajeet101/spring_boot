package com.satya.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductResponse {
        private Long productId;
        private String productName;
        private double productPrice;
        private String notFound;
}
