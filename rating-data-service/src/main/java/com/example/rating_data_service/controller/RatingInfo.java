package com.example.rating_data_service.controller;

import com.example.rating_data_service.model.Rating;
import com.example.rating_data_service.model.UserRating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingInfo {

    @GetMapping("/{movieId}")
    public Rating getMovieInfo(@PathVariable String movieId) {
        return new Rating(movieId, 4);
    }
    @GetMapping("/users/{userId}")
    public UserRating getRating(@PathVariable String userId) {
       List<Rating> ratings =  Arrays.asList(
            new Rating("m1", 4),
            new Rating("m2", 3),
            new Rating("m3", 2)
        );
        UserRating userRating = new UserRating();
        userRating.setUserRating(ratings);
        return userRating;
    }
}