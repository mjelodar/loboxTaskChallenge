package repository.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table
public class BasicMovie {
    @Id
    private String tconst;
    private String titleType;
    private String primaryTitle;
    private String originalTitle;
    private Boolean isAdult;
    private String startYear;
    private String endYear;
    private String runTimeMinutes;
    private String genre;
    private String directors;
    private String writers;
    private String averageRating;
    private String numVotes;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "basicMovie")
    private Set<Principals> principals;
}
