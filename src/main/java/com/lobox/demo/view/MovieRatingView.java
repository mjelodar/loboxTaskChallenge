package com.lobox.demo.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
public class MovieRatingView {
    private String tconst;
    private double rating;
    private int numberOfVotes;
}

