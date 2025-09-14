package com.example.movie_catalog_service.controller;

import com.example.movie_catalog_service.models.CatalogItem;
import com.example.movie_catalog_service.models.Movie;
import com.example.movie_catalog_service.models.Rating;
import com.example.movie_catalog_service.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WebClient.Builder builder;
    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        //Get Ratings
        UserRating userRating = restTemplate.getForObject("http://RATING-DATA-SERVICE/rating/users/"+userId, UserRating.class);
        return userRating.getUserRating().stream().map((r)-> {
            Movie movie =
                    restTemplate.getForObject("http://MOVIE-INFO-SERVICE/movie/"+ r.getMovieId(), Movie.class);
            /*builder.build()
                    .get()
                    .uri("http://localhost:8082/movie/"+ r.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();*/
            return new CatalogItem(movie.getName(), "desc", r.getRating());
        }).collect(Collectors.toList());
    }
}
