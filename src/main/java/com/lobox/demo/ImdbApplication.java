package com.lobox.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

@SpringBootApplication
//@EnableBatchProcessing
public class ImdbApplication implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;


	public ImdbApplication(JobLauncher jobLauncher, ApplicationContext applicationContext) {
		this.jobLauncher = jobLauncher;
		this.applicationContext = applicationContext;
	}

	public static void main(String[] args) {
		SpringApplication.run(ImdbApplication.class, args);
	}

	public void run(String... args) throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		Job job = applicationContext.getBean("importJob", Job.class);
		var jobExecution = jobLauncher.run(job, jobParameters);

		var batchStatus = jobExecution.getStatus();
		while (batchStatus.isRunning()) {
			System.out.println("Still running...");
			Thread.sleep(5000L);
		}
	}

}
