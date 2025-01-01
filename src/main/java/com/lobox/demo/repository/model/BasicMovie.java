package com.lobox.demo.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@NamedEntityGraph(name = "basic.crew",
//        attributeNodes = @NamedAttributeNode("crew"))
@Table
public class BasicMovie {
    @Id
    private String tconst;
    private String titleType;

    @Column(length = 500)
    private String primaryTitle;

    @Column(length = 500)
    private String originalTitle;
    private Boolean isAdult;
    private String startYear;
    private String endYear;
    private String runTimeMinutes;
    private String genre;


}
