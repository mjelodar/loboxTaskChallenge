package com.lobox.demo.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final Step basicMovieStep;
    private final Step crewStep;

//    @Value("${title.basics.file.path}")
//    private String filePath;

    public BatchConfig(JobRepository jobRepository,
                       BasicStepConfiguration basicStepConfiguration,
                       CrewStepConfiguration crewStepConfiguration) {
        this.jobRepository = jobRepository;

        this.basicMovieStep = basicStepConfiguration.createStep();
        this.crewStep = crewStepConfiguration.createStep();
    }


    @Bean(name = "importJob")
    public Job importJob() {
        return new JobBuilder("import job", jobRepository)
                .start(basicMovieStep)
                .next(crewStep)
                .build();
    }












}


