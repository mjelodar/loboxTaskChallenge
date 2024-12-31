package com.lobox.demo.service;

import com.lobox.demo.repository.BasicMovieJpaRepository;
import com.lobox.demo.repository.model.BasicMovie;
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
public class BasicStepConfiguration extends StepConfiguration<BasicMovie> {

    private final BasicMovieJpaRepository basicMovieJpaRepository;


    public BasicStepConfiguration(BasicMovieJpaRepository basicMovieJpaRepository, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        super(jobRepository, transactionManager);
        this.basicMovieJpaRepository = basicMovieJpaRepository;
    }


    @Override
    @Bean(name = "crewReader")
    protected FlatFileItemReader<BasicMovie> reader() {
        return new FlatFileItemReaderBuilder<BasicMovie>()
                .name("basicItemReader")
                .resource(new ClassPathResource("test.tsv"))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names("tconst","titleType","primaryTitle","originalTitle","isAdult","startYear","endYear","runtimeMinutes","genres")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(BasicMovie.class);
                    }
                }).build();
    }

    @Override
    @Bean(name = "crewProcessor")
    public ItemProcessor<BasicMovie, BasicMovie> processor() {
        return basicMovie -> {
            if (Objects.equals(basicMovie.getEndYear(), "\\N"))
                basicMovie.setEndYear(null);

            return basicMovie;
        };
    }

    @Override
    @Bean(name = "crewWriter")
    public RepositoryItemWriter<BasicMovie> writer() {
        RepositoryItemWriter<BasicMovie> writer = new RepositoryItemWriter<>();
        writer.setRepository(basicMovieJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Override
    @Bean(name = "crewStep")
    public Step step(ItemReader<BasicMovie> reader,
                          ItemProcessor<BasicMovie, BasicMovie> processor,
                          ItemWriter<BasicMovie> writer) {
        return new StepBuilder("basicStep", jobRepository)
                .<BasicMovie, BasicMovie>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
