package com.lobox.demo.repository;

import com.lobox.demo.repository.model.BasicMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicMovieJpaRepository extends JpaRepository<BasicMovie, String> {
}
