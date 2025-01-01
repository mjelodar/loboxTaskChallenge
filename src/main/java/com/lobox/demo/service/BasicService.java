package com.lobox.demo.service;

import com.lobox.demo.repository.BasicMovieJpaRepository;
import com.lobox.demo.view.BasicMoviePlusRating;
import com.lobox.demo.view.BasicView;
import com.lobox.demo.view.BestMovieOfYears;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasicService {
    BasicMovieJpaRepository basicMovieJpaRepository;

    public BasicService(BasicMovieJpaRepository basicMovieJpaRepository) {
        this.basicMovieJpaRepository = basicMovieJpaRepository;
    }

    public List<BasicView> findAll() {
        return (List<BasicView>) basicMovieJpaRepository.findAll().
                stream().
                map(basicMovie -> BasicView.builder()
                                .tconst(basicMovie.getTconst())
                                .titleType(basicMovie.getTitleType())
                                .primaryTitle(basicMovie.getPrimaryTitle())
                                .originalTitle(basicMovie.getOriginalTitle())
                                .isAdult(basicMovie.getIsAdult())
                                .startYear(basicMovie.getStartYear())
                                .endYear(basicMovie.getEndYear())
                                .runTimeMinutes(basicMovie.getRunTimeMinutes())
                                .genre(basicMovie.getGenre())
                                .build()).toList();
    }

    public List<BestMovieOfYears> findByGenre(String genre) {
        return basicMovieJpaRepository.findByGenre(genre);

    }
}
