package service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import repository.model.BasicMovie;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilder jobBuilder;

    @Autowired
    private StepBuilder stepBuilder;

    @Bean
    public Job importJob(Step partitionStep) {
        return jobBuilder.get("importJob")
                .start(partitionStep)
                .build();
    }

    @Bean
    public Step partitionStep(TaskExecutorPartitionHandler partitionHandler) {
        return stepBuilder.get("partitionStep")
                .partitioner("slaveStep", partitioner())
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        return new MultiResourcePartitioner();
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(Step slaveStep) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(slaveStep);
        handler.setTaskExecutor(taskExecutor());
        return handler;
    }

    @Bean
    public Step slaveStep(ItemReader<BasicMovie> reader,
                          ItemProcessor<BasicMovie, Movie> processor,
                          ItemWriter<Movie> writer) {
        return stepBuilder.get("slaveStep")
                .<BasicMovie, Movie>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("partition-thread");
    }
}

