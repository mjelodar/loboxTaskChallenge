package com.lobox.demo.repository;

import com.lobox.demo.repository.model.BasicMovie;
import com.lobox.demo.view.BestMovieOfYears;
import com.lobox.demo.view.MovieWith2CommonActors;
import com.lobox.demo.view.SameAliveDirectorWriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BasicMovieJpaRepository extends JpaRepository<BasicMovie, String> {
    @Query("""
            select new com.lobox.demo.view.BestMovieOfYears(b.primaryTitle, b.startYear, b.genre, r.averageRating, r.numVotes) \
            from BasicMovie b \
            inner join Rating r ON b.tconst = r.tconst \
            where (b.startYear, b.genre, (r.averageRating * r.numVotes)) in (\
                select b2.startYear, b2.genre, max(r2.averageRating * r2.numVotes)\
                from BasicMovie b2 \
                inner join Rating r2 ON b2.tconst = r2.tconst \
                where b2.genre=?1 \
                group by b2.startYear, b2.genre)""")
    List<BestMovieOfYears> findBestMovieOfYearsByGenre(String genre);

    @Query("""
            select new com.lobox.demo.view.SameAliveDirectorWriter(b.primaryTitle, n.primaryName, n.birthYear) \
            from BasicMovie b \
            inner join Crew c ON b.tconst = c.tconst \
            inner join Principals p ON c.tconst = p.tconst \
            inner join Names n ON p.nconst = n.nconst \
            where c.directors=c.writers and \
                    p.category = 'director' and \
                    n.deathYear is null and n.birthYear is not null""")
    List<SameAliveDirectorWriter> findMovieWithSameAliveDirectorWriter();

    @Query("""
            select new com.lobox.demo.view.MovieWith2CommonActors(b.primaryTitle) \
            from BasicMovie b \
            where b.tconst in ( \
                select p1.tconst \
                from Principals p1 \
                inner join Names n1  on p1.nconst=n1.nconst \
                where (p1.category='actor' or p1.category='actress') and \
                n1.primaryName =?1 and \
                p1.tconst in ( \
                    select p2.tconst \
                    from Principals p2 \
                    inner join Names n2 on p2.nconst=n2.nconst \
                    where (p2.category='actor' or p2.category='actress') and \
                    n2.primaryName =?2))""")
    List<MovieWith2CommonActors> findMovieWith2CommonActor(String actor1, String actor2);
}
