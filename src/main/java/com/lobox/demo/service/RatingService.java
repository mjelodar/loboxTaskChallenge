package com.lobox.demo.service;

import com.lobox.demo.repository.BasicMovieJpaRepository;
import com.lobox.demo.repository.RatingJpaRepository;
import com.lobox.demo.view.BasicMoviePlusRating;
import com.lobox.demo.view.BasicView;
import com.lobox.demo.view.MovieRatingView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    RatingJpaRepository ratingJpaRepository;

    public RatingService(RatingJpaRepository ratingJpaRepository) {
        this.ratingJpaRepository = ratingJpaRepository;
    }

    public List<MovieRatingView> findAll() {
        return ratingJpaRepository.findAll().
                stream().
                map(rating -> new MovieRatingView(rating.getTconst(), rating.getAverageRating(), rating.getNumVotes())).toList();

    }

}

