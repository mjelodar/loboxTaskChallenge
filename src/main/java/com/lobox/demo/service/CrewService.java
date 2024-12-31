package com.lobox.demo.service;

import com.lobox.demo.repository.CrewJpaRepository;
import com.lobox.demo.repository.RatingJpaRepository;
import com.lobox.demo.view.CrewView;
import com.lobox.demo.view.MovieRatingView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrewService {
    CrewJpaRepository crewJpaRepository;

    public CrewService(CrewJpaRepository crewJpaRepository) {
        this.crewJpaRepository = crewJpaRepository;
    }

    public List<CrewView> findAll() {
        return crewJpaRepository.findAll().
                stream().
                map(crew -> new CrewView(crew.getTconst(), crew.getDirectors(), crew.getWriters())).toList();

    }

}
