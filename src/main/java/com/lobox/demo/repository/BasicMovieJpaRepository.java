package com.lobox.demo.repository;

import com.lobox.demo.repository.model.BasicMovie;
import com.lobox.demo.view.BasicMoviePlusRating;
import com.lobox.demo.view.BestMovieOfYears;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BasicMovieJpaRepository extends JpaRepository<BasicMovie, String> {
    @Query("""
            select new com.lobox.demo.view.BestMovieOfYears(b.primaryTitle, b.startYear, b.genre, r.averageRating*r.numVotes) \
            from BasicMovie b \
            inner join Rating r ON b.tconst = r.tconst \
            where (b.startYear, b.genre, (r.averageRating * r.numVotes)) in (\
                select b2.startYear, b2.genre, max(r2.averageRating * r2.numVotes)\
                from BasicMovie b2 \
                inner join Rating r2 ON b2.tconst = r2.tconst \
                where b2.genre=?1 \
                group by b2.startYear, b2.genre)""")
    List<BestMovieOfYears> findByGenre(String genre);

}
