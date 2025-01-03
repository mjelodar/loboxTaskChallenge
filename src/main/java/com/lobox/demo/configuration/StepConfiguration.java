package com.lobox.demo.configuration;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class StepConfiguration<T> {

    protected final JobRepository jobRepository;
    protected final PlatformTransactionManager transactionManager;

    protected StepConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    public Step createStep(){
        return step(reader(), processor(), writer());
    }

    protected abstract ItemReader<T> reader();

    protected abstract ItemProcessor<T, T> processor();

    protected abstract ItemWriter<T> writer();

    protected abstract Step step(ItemReader<T> reader, ItemProcessor<T, T> processor, ItemWriter<T> writer);

}
