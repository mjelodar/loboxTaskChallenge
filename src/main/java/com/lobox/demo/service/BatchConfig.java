package com.lobox.demo.service;

import com.lobox.demo.repository.BasicMovieJpaRepository;
import com.lobox.demo.repository.CrewJpaRepository;
import com.lobox.demo.repository.model.Crew;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import com.lobox.demo.repository.model.BasicMovie;

import java.util.Objects;


@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final BasicMovieJpaRepository basicMovieJpaRepository;
    private final CrewJpaRepository crewJpaRepository;

//    @Value("${title.basics.file.path}")
//    private String filePath;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, BasicMovieJpaRepository basicMovieJpaRepository, CrewJpaRepository crewJpaRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.basicMovieJpaRepository = basicMovieJpaRepository;
        this.crewJpaRepository = crewJpaRepository;
    }


    @Bean(name = "importJob")
    public Job importJob() {
        return new JobBuilder("import job", jobRepository)
                .start(movieStep(basicReader(),processor(), basicItemWriter()))
                .next(crewStep(crewReader(),crewProcessor(), crewItemWriter()))
                .build();
    }

    @Bean
    public Step movieStep(ItemReader<BasicMovie> reader,
                     ItemProcessor<BasicMovie, BasicMovie> processor,
                     ItemWriter<BasicMovie> writer) {
        return new StepBuilder("basicStep", jobRepository)
                .<BasicMovie, BasicMovie>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<BasicMovie> basicReader() {
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

    @Bean
    public ItemProcessor<BasicMovie, BasicMovie> processor() {
        return basicMovie -> {
            if (Objects.equals(basicMovie.getEndYear(), "\\N"))
                basicMovie.setEndYear(null);

            return basicMovie;
        };
    }

    @Bean
    public RepositoryItemWriter<BasicMovie> basicItemWriter() {
        RepositoryItemWriter<BasicMovie> writer = new RepositoryItemWriter<>();
        writer.setRepository(basicMovieJpaRepository);
        writer.setMethodName("save");
        return writer;

    }

    @Bean
    public Step crewStep(ItemReader<Crew> reader,
                          ItemProcessor<Crew, Crew> processor,
                          ItemWriter<Crew> writer) {
        return new StepBuilder("crewStep", jobRepository)
                .<Crew, Crew>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Crew> crewReader() {
        return new FlatFileItemReaderBuilder<Crew>()
                .name("crewItemReader")
                .resource(new ClassPathResource("crewTest.tsv"))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names("tconst","directors","writers")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Crew.class);
                    }
                }).build();
    }


    @Bean
    public ItemProcessor<Crew, Crew> crewProcessor() {
        return crew -> {
//            BasicMovie basicMovie = basicMovieJpaRepository.findById(crew.getBasicMovie().getTconst()).orElse(null);

            if (Objects.equals(crew.getDirectors(), "\\N"))
                crew.setDirectors(null);

            if (Objects.equals(crew.getWriters(), "\\N"))
                crew.setWriters(null);

//            crew.setBasicMovie(basicMovie);

            return crew;
        };
    }

    @Bean
    public RepositoryItemWriter<Crew> crewItemWriter() {
        RepositoryItemWriter<Crew> writer = new RepositoryItemWriter<>();
        writer.setRepository(crewJpaRepository);
        writer.setMethodName("save");
        return writer;

    }
}


