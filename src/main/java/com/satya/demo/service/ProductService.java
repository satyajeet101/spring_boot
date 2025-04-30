package com.satya.demo.service;

import com.satya.demo.config.PropertiesReader;
import com.satya.demo.dto.ProductResponse;
import com.satya.demo.entity.ProductEntity;
import com.satya.demo.exception.ProductNotFoundException;
import com.satya.demo.repository.ProductRepo;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepo productRepo;
    @Autowired
    PropertiesReader propertiesReader;
    @Autowired
    private ModelMapper modelMapper;
    @PostConstruct
    public void loadAfterBeansCreatedAndInjected() {
        System.out.println("Beans created and injected.....");
        System.out.println("Profile in use : "+propertiesReader.getProfileInUse());
        System.out.println("My Name : "+propertiesReader.getMyNameFromPropertyFile());
        System.out.println("My Age : "+propertiesReader.getMyAgeFromPropertyFile());
    }
    public List<ProductResponse> getProduct() {
        List<ProductEntity> productEntityList = productRepo.findAll();
        return productEntityList.stream().map((x)-> modelMapper.map(x, ProductResponse.class)).toList();
    }
    public ProductEntity addProduct(ProductEntity product) {
        return productRepo.save(product);
    }

    public String updateProduct(ProductEntity newProduct) {
        productRepo.save(newProduct);
        return "Update Successfully!!";
    }

    public String removeProduct(Long id) {
        productRepo.deleteById(id);
        return "Removed Successfully!!";
    }

    public ProductResponse getProductById(Long id) {
        ProductResponse productResponse = new ProductResponse();;
        try {
            ProductEntity productEntity = productRepo.findById(id)
                    .orElseThrow(()->new ProductNotFoundException("Product not found for ID : "+ id));
            productResponse.setProductId(productEntity.getProductId());
            productResponse.setProductPrice(productEntity.getProductPrice());
            productResponse.setProductName(productEntity.getProductName());
        } catch (ProductNotFoundException e) {
            productResponse.setNotFound(e.getMessage());
        }
        return productResponse;
    }
}