package com.lobox.demo.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import com.lobox.demo.repository.model.BasicMovie;

import java.io.File;


@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${title.basics.file.path}")
    private String filePath;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }


    @Bean(name = "importJob")
    public Job importJob() {
        return new JobBuilder("import job", jobRepository)
                .start(movieStep(basicReader(),processor(), basicItemWriter()))
                .build();
    }

    @Bean
    public Step movieStep(ItemReader<BasicMovie> reader,
                     ItemProcessor<BasicMovie, BasicMovie> processor,
                     ItemWriter<BasicMovie> writer) {
        return new StepBuilder("step", jobRepository)
                .<BasicMovie, BasicMovie>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
//
//    @Bean
//    public ItemReader<BasicMovie> reader(Resource[] resources) {
//        MultiResourceItemReader<BasicMovie> reader = new MultiResourceItemReader<>();
//        reader.setResources(resources);
//        reader.setDelegate(fileReader());
//        return reader;
//    }
//
//    @Bean
//    @StepScope
//    public Resource[] resources() throws IOException {
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        return resolver.getResources("file:D:\\IMDB-dataset\\*.tsv\\*.tsv");
//    }

    @Bean
    public FlatFileItemReader<BasicMovie> basicReader() {
//        FlatFileItemReader<BasicMovie> reader = new FlatFileItemReader<>();
        return new FlatFileItemReaderBuilder<BasicMovie>()
                .name("basicItemReader")
                .resource(new ClassPathResource("test.tsv"))
                .linesToSkip(1)
                .delimited().delimiter("\t")
                .names(new String[] {"tconst","titleType","primaryTitle","originalTitle","isAdult","startYear","endYear","runtimeMinutes","genres"})
                        .fieldSetMapper(new BeanWrapperFieldSetMapper<BasicMovie>() {
                            {
                                setTargetType(BasicMovie.class);
                            }
                        }).build();
    }

    @Bean
    public ItemProcessor<BasicMovie, BasicMovie> processor() {
            return basicMovie -> basicMovie;
    }

    @Bean
    public ItemWriter<BasicMovie> basicItemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}


