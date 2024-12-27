package repository.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table
public class Names {
    @Id
    private String nconst;
    private String primaryName;
    private String birthYear;
    private String deathYear;
    private String primaryProfession;
    private String knownForTitles;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "names")
    private Set<Principals> principals;
}
