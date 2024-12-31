package com.lobox.demo.view;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BasicCrewView extends BasicView {
    private String tconst;
    private String directors;
    private String writers;
}
