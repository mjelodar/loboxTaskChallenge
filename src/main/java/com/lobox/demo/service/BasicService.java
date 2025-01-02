package com.lobox.demo.service;

import com.lobox.demo.repository.BasicMovieJpaRepository;
import com.lobox.demo.view.BasicView;
import com.lobox.demo.view.BestMovieOfYears;
import com.lobox.demo.view.MovieWith2CommonActors;
import com.lobox.demo.view.SameAliveDirectorWriter;
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

    public List<BestMovieOfYears> findBestMovieOfYearsByGenre(String genre) {
        return basicMovieJpaRepository.findBestMovieOfYearsByGenre(genre);
    }

    public List<SameAliveDirectorWriter> findMovieWithAliveSameDirectorWriter() {
        return basicMovieJpaRepository.findMovieWithSameAliveDirectorWriter();
    }

    public List<MovieWith2CommonActors> findMovieWith2CommonActor(String actor1, String actor2) {
        return basicMovieJpaRepository.findMovieWith2CommonActor(actor1, actor2);
    }
}
