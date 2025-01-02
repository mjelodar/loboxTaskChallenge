package com.lobox.demo.configuration;

import com.lobox.demo.repository.PrincipalJpaRepository;
import com.lobox.demo.repository.model.Principals;
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
public class PrincipalStepConfiguration extends StepConfiguration<Principals> {

    private final PrincipalJpaRepository principalJpaRepository;

    @Value("${title.principal.file.path}")
    private String filePath;

    public PrincipalStepConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, PrincipalJpaRepository principalJpaRepository) {
        super(jobRepository, transactionManager);
        this.principalJpaRepository = principalJpaRepository;
    }


    @Override
    @Bean(name = "principalReader")
    protected FlatFileItemReader<Principals> reader() {
        return new FlatFileItemReaderBuilder<Principals>()
                .name("principalItemReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names("tconst","ordering","nconst","category","job","characters")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Principals.class);
                    }
                }).build();
    }

    @Override
    @Bean(name = "principalProcessor")
    public ItemProcessor<Principals, Principals> processor() {
        return principals -> {
            if (Objects.equals(principals.getCategory(), "\\N"))
                principals.setCategory(null);

            if (Objects.equals(principals.getCharacters(), "\\N"))
                principals.setCharacters(null);

            if (Objects.equals(principals.getJob(), "\\N"))
                principals.setJob(null);

            return principals;
        };
    }

    @Override
    @Bean(name = "principalWriter")
    public RepositoryItemWriter<Principals> writer() {
        RepositoryItemWriter<Principals> writer = new RepositoryItemWriter<>();
        writer.setRepository(principalJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Override
    @Bean(name = "principalStep")
    public Step step(ItemReader<Principals> reader,
                          ItemProcessor<Principals, Principals> processor,
                          ItemWriter<Principals> writer) {
        return new StepBuilder("principalStep", jobRepository)
                .<Principals, Principals>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
