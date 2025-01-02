package com.lobox.demo.controller;

import com.google.gson.Gson;
import com.lobox.demo.service.BasicService;
import com.lobox.demo.service.CrewService;
import com.lobox.demo.service.RatingService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    BasicService basicService;

    @Autowired
    RatingService ratingService;

    @Autowired
    CrewService crewService;

    @PostMapping("/importdata")
    public void importData() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        var batchStatus = jobExecution.getStatus();
        while (batchStatus.isRunning()) {
            System.out.println("Still running...");
            Thread.sleep(5000L);
        }
    }

    @GetMapping("/get/basic")
    public String getBasicMovie(){
        return new Gson().toJson(basicService.findAll());
    }

    @GetMapping("/get/bestMovie/{genre}")
    public String getBasicMovie(@PathVariable String genre){
        return new Gson().toJson(basicService.findBestMovieOfYearsByGenre(genre));
    }

    @GetMapping("/get/rating")
    public String getMovieRating(){
        return new Gson().toJson(ratingService.findAll());
    }

    @GetMapping("/get/crew")
    public String getMovieCrew(){
        return new Gson().toJson(crewService.findAll());
    }

    @GetMapping("/get/movie/sameAliveDirectorWriter")
    public String getMovieThatHaveSameAliveDirectorAndWriter(){
        return new Gson().toJson(basicService.findMovieWithAliveSameDirectorWriter());
    }
}
