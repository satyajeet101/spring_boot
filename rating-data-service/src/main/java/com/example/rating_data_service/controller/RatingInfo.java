package com.example.rating_data_service.controller;

import com.example.rating_data_service.model.Rating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/rating")
public class RatingInfo {

    @GetMapping("/{movieId}")
    public Rating getMovieInfo(@PathVariable String movieId) {
        return new Rating(movieId, 4);
    }
}