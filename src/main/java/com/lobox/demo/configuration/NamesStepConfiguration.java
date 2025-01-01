package com.lobox.demo.configuration;

import com.lobox.demo.repository.NamesJpaRepository;
import com.lobox.demo.repository.model.Names;
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
public class NamesStepConfiguration extends StepConfiguration<Names> {

    private final NamesJpaRepository namesJpaRepository;


    public NamesStepConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, NamesJpaRepository namesJpaRepository) {
        super(jobRepository, transactionManager);
        this.namesJpaRepository = namesJpaRepository;
    }


    @Override
    @Bean(name = "nameReader")
    protected FlatFileItemReader<Names> reader() {
        return new FlatFileItemReaderBuilder<Names>()
                .name("nameItemReader")
                .resource(new ClassPathResource("nameTest.tsv"))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names("nconst","primaryName","birthYear","deathYear", "primaryProfession", "knownForTitles")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Names.class);
                    }
                }).build();
    }

    @Override
    @Bean(name = "nameProcessor")
    public ItemProcessor<Names, Names> processor() {
        return names -> {
            if (Objects.equals(names.getBirthYear(), "\\N"))
                names.setBirthYear(null);

            if (Objects.equals(names.getDeathYear(), "\\N"))
                names.setDeathYear(null);

            if (Objects.equals(names.getPrimaryProfession(), "\\N"))
                names.setPrimaryProfession(null);

            if (Objects.equals(names.getKnownForTitles(), "\\N"))
                names.setKnownForTitles(null);

            return names;
        };
    }

    @Override
    @Bean(name = "nameWriter")
    public RepositoryItemWriter<Names> writer() {
        RepositoryItemWriter<Names> writer = new RepositoryItemWriter<>();
        writer.setRepository(namesJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Override
    @Bean(name = "nameStep")
    public Step step(ItemReader<Names> reader,
                          ItemProcessor<Names, Names> processor,
                          ItemWriter<Names> writer) {
        return new StepBuilder("nameStep", jobRepository)
                .<Names, Names>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
