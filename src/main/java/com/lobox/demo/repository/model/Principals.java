package com.lobox.demo.repository.model;

import jakarta.persistence.*;

@Entity
@Table
public class Principals {
    @Id
    private int ordering;


    private String category;
    private String job;
    private String characters;

}
