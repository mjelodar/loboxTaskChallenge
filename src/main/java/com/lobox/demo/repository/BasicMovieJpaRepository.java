package com.lobox.demo.repository;

import com.lobox.demo.repository.model.BasicMovie;
import com.lobox.demo.view.BasicMoviePlusRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BasicMovieJpaRepository extends JpaRepository<BasicMovie, String> {
    @Query(value = "select select new com.lobox.demo.view.BasicMoviePlusRating(b.tconst, b.primaryTitle, b.startYear, b.genre, r.averageRating, r.numVotes) " +
            "from BasicMovie b " +
            "inner join Rating r ON b.tconst = r.tconst " +
            "group by b.primaryTitle, b.startYear, b.genre" +
            "where b.genre=?1")
    List<BasicMoviePlusRating> findByGenre(String genre);

}
