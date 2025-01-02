package com.lobox.demo.configuration;

import com.lobox.demo.repository.CrewJpaRepository;
import com.lobox.demo.repository.model.Crew;
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
public class CrewStepConfiguration extends StepConfiguration<Crew> {

    private final CrewJpaRepository crewJpaRepository;

    @Value("${title.crew.file.path}")
    private String filePath;

    public CrewStepConfiguration(CrewJpaRepository crewJpaRepository, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        super(jobRepository, transactionManager);
        this.crewJpaRepository = crewJpaRepository;
    }


    @Override
    @Bean("crewReader")
    protected FlatFileItemReader<Crew> reader() {
        return new FlatFileItemReaderBuilder<Crew>()
                .name("crewItemReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names("tconst","directors","writers")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Crew.class);
                    }
                }).build();
    }

    @Override
    @Bean("crewProcessor")
    public ItemProcessor<Crew, Crew> processor() {
        return crew -> {
            if (Objects.equals(crew.getDirectors(), "\\N"))
                crew.setDirectors(null);

            if (Objects.equals(crew.getWriters(), "\\N"))
                crew.setWriters(null);

            return crew;
        };
    }



    @Override
    @Bean("crewWriter")
    public RepositoryItemWriter<Crew> writer() {
        RepositoryItemWriter<Crew> writer = new RepositoryItemWriter<>();
        writer.setRepository(crewJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Override
    @Bean("crewStep")
    public Step step(ItemReader<Crew> reader,
                         ItemProcessor<Crew, Crew> processor,
                         ItemWriter<Crew> writer) {
        return new StepBuilder("crewStep", jobRepository)
                .<Crew, Crew>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
