package com.example.rating_data_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class UserRating {
    private List<Rating> userRating;
}
