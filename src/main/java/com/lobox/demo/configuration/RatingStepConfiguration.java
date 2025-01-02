package com.lobox.demo.configuration;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
public class RatingStepConfiguration extends StepConfiguration<Rating> {

    private final RatingJpaRepository ratingJpaRepository;

    @Value("${title.rating.file.path}")
    private String filePath;

    public RatingStepConfiguration(RatingJpaRepository ratingJpaRepository, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        super(jobRepository, transactionManager);
        this.ratingJpaRepository = ratingJpaRepository;
    }


    @Override
    @Bean("ratingReader")
    protected FlatFileItemReader<Rating> reader() {
        return new FlatFileItemReaderBuilder<Rating>()
                .name("ratingItemReader")
                .resource(new FileSystemResource(filePath))
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
    @Bean("ratingProcessor")
    public ItemProcessor<Rating, Rating> processor() {
        return rating -> {
            if (Objects.equals(rating.getAverageRating(), "\\N"))
                rating.setAverageRating(0D);

            if (Objects.equals(rating.getNumVotes(), "\\N"))
                rating.setNumVotes(0);

            return rating;
        };
    }



    @Override
    @Bean("ratingWriter")
    public RepositoryItemWriter<Rating> writer() {
        RepositoryItemWriter<Rating> writer = new RepositoryItemWriter<>();
        writer.setRepository(ratingJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Override
    @Bean("ratingStep")
    public Step step(ItemReader<Rating> reader,
                         ItemProcessor<Rating, Rating> processor,
                         ItemWriter<Rating> writer) {
        return new StepBuilder("ratingStep", jobRepository)
                .<Rating, Rating>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
