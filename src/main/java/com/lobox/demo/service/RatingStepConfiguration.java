package com.lobox.demo.service;

import com.lobox.demo.repository.RatingJpaRepository;
import com.lobox.demo.repository.model.Rating;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
public class RatingStepConfiguration extends StepConfiguration<Rating> {

    private final RatingJpaRepository ratingJpaRepository;

    public RatingStepConfiguration(RatingJpaRepository ratingJpaRepository,
                                   JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) {
        super(jobRepository, transactionManager);
        this.ratingJpaRepository = ratingJpaRepository;
    }


    @Override
    @Bean(name = "ratingReader")
    protected FlatFileItemReader<Rating> reader() {
        return new FlatFileItemReaderBuilder<Rating>()
                .name("ratingItemReader")
                .resource(new ClassPathResource("ratingTest.tsv"))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names("tconst","averageRating","numVotes")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Rating.class);
                    }
                }).build();
    }

    @Override
    @Bean(name = "ratingProcessor")
    public ItemProcessor<Rating, Rating> processor() {
        return rating -> {
            if (Objects.equals(rating.getAverageRating(), "\\N"))
                rating.setAverageRating(null);

            if (Objects.equals(rating.getNumVotes(), "\\N"))
                rating.setNumVotes(null);

            return rating;
        };
    }



    @Override
    @Bean(name = "ratingWriter")
    public RepositoryItemWriter<Rating> writer() {
        RepositoryItemWriter<Rating> writer = new RepositoryItemWriter<>();
        writer.setRepository(ratingJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Override
    @Bean(name = "ratingStep")
    public Step slaveStep(ItemReader<Rating> reader,
                         ItemProcessor<Rating, Rating> processor,
                         ItemWriter<Rating> writer) {
        return new StepBuilder("ratingStep", jobRepository)
                .<Rating, Rating>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

//    @Override
//    @Bean(name = "ratingPartitionHandler")
//    public TaskExecutorPartitionHandler partitionHandler(Step slaveStep) {
//        partitionHandler.setStep(slaveStep);
//        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        partitionHandler.setGridSize(500); // Adjust for the number of files/threads
//        return partitionHandler;
//    }
}
