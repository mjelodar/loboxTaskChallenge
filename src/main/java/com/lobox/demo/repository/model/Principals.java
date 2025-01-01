package com.lobox.demo.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(PrincipalPK.class)
@Table
public class Principals {
    @Id
    private int ordering;
    @Id
    private String tconst;

    private String nconst;
    private String category;
    private String job;
    private String characters;

}
