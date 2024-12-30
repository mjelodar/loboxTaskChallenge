package com.lobox.demo.service;

import lombok.Data;

import java.util.List;

@Data
public class CompositeModel {

    private String nconst;
    private String primaryName;
    private String birthYear;
    private String deathYear;
    private String primaryProfession;
    private String knownForTitles;
    private List<String> basics;
    private List<String> crews;
    private List<String> principals;
    private List<String> ratings;
}
