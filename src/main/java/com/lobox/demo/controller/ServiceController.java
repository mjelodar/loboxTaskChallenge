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
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ServiceController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private BasicService basicService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CrewService crewService;

    private static AtomicLong requestCounter = new AtomicLong(0);

    @PostMapping("/importdata")
    public void importData() throws Exception{
        requestCounter.incrementAndGet();
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
        requestCounter.incrementAndGet();
        return new Gson().toJson(basicService.findAll());
    }

    @GetMapping("/get/bestMovieOnEachYear")
    public String getBasicMovie(@RequestParam String genre){
        requestCounter.incrementAndGet();
        return new Gson().toJson(basicService.findBestMovieOfYearsByGenre(genre));
    }

    @GetMapping("/get/rating")
    public String getMovieRating(){
        requestCounter.incrementAndGet();
        return new Gson().toJson(ratingService.findAll());
    }

    @GetMapping("/get/crew")
    public String getMovieCrew(){
        requestCounter.incrementAndGet();
        return new Gson().toJson(crewService.findAll());
    }

    @GetMapping("/get/movie/sameAliveDirectorWriter")
    public String getMovieThatHaveSameAliveDirectorAndWriter() {
        requestCounter.incrementAndGet();
        return new Gson().toJson(basicService.findMovieWithAliveSameDirectorWriter());
    }

    @GetMapping("/get/movie/twoCommonActors")
    public String getMovieThatHaveSameAliveDirectorAndWriter(@RequestParam String actor1, @RequestParam String actor2){
        requestCounter.incrementAndGet();
        return new Gson().toJson(basicService.findMovieWith2CommonActor(actor1, actor2));
    }

    @GetMapping("/get/requestCounter")
    public String getRequestCounter(){
        StringBuffer sb = new StringBuffer();
        sb.append("number of request: ").append(String.valueOf(requestCounter.get()));
        return new Gson().toJson(sb.toString());
    }
}
