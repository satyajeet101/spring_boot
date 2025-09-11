package com.example.rating_data_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rating {
    String movieId;
    int rating;
}
