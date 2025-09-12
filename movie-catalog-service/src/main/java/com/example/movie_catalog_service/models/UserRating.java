package com.example.movie_catalog_service.models;

import lombok.Data;

import java.util.List;

@Data
public class UserRating {
    private List<Rating> userRating;
}
