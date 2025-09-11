package com.example.movie_catalog_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
@AllArgsConstructor
public class CatalogItem {
    String name;
    String desc;
    int rating;
}
