package com.lobox.demo.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Crew {
    @Id
    private String tconst;

    @Column(length = 500)
    private String directors;

    @Column(length = 500)
    private String writers;

}
