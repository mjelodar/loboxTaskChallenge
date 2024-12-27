package repository.model;

import jakarta.persistence.*;

@Entity
@Table
public class Principals {
    @Id
    private int ordering;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basicMovieId", referencedColumnName = "tconst")
    private BasicMovie basicMovie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nameId", referencedColumnName = "nconst")
    private Names names;

    private String category;
    private String job;
    private String characters;

}
