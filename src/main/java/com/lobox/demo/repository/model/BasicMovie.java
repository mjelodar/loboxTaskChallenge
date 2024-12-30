package com.lobox.demo.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
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


//    private String directors;
//    private String writers;
//    private String averageRating;
////    private String numVotes;
//
//    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "basicMovie")
//    private List<Principals> principals;
}
