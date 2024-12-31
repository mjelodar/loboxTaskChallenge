package com.lobox.demo.service;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class StepConfiguration<T> {

    protected final JobRepository jobRepository;
    protected final PlatformTransactionManager transactionManager;

    protected StepConfiguration(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }



    public Step createStep(){
        return masterStep(jobRepository);
    }

    protected abstract FlatFileItemReader<T> reader();

    protected abstract ItemProcessor<T, T> processor();

    protected abstract RepositoryItemWriter<T> writer();

    @Bean
    public Step masterStep(JobRepository jobRepository) {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("slaveStep", new FilePartitioner())
                .partitionHandler(partitionHandler(slaveStep(reader(), processor(), writer())))
                .build();
    }

    public abstract Step slaveStep(ItemReader<T> reader, ItemProcessor<T, T> processor, ItemWriter<T> writer);

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(Step slaveStep) {
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();

        taskExecutorPartitionHandler.setStep(slaveStep);
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        taskExecutorPartitionHandler.setGridSize(2); // Adjust for the number of files/threads
        return taskExecutorPartitionHandler;
    }
}
