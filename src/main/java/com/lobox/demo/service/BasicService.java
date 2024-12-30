package com.lobox.demo.service;

import com.lobox.demo.repository.BasicMovieJpaRepository;
import com.lobox.demo.repository.model.BasicMovie;
import com.lobox.demo.view.BasicView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasicService {
    BasicMovieJpaRepository basicMovieJpaRepository;

    public BasicService(BasicMovieJpaRepository basicMovieJpaRepository) {
        this.basicMovieJpaRepository = basicMovieJpaRepository;
    }

    public List<BasicView> findAll() {
        return basicMovieJpaRepository.findAll().
                stream().
                map(basicMovie -> new BasicView(basicMovie.getTconst(),
                        basicMovie.getTitleType(),
                        basicMovie.getPrimaryTitle(),
                        basicMovie.getOriginalTitle(),
                        basicMovie.getIsAdult(),
                        basicMovie.getStartYear(),
                        basicMovie.getEndYear(),
                        basicMovie.getRunTimeMinutes(),
                        basicMovie.getGenre()))
                .toList();
    }
}
