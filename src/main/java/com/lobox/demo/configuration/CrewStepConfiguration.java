package com.lobox.demo.configuration;

import com.lobox.demo.repository.CrewJpaRepository;
import com.lobox.demo.repository.model.BasicMovie;
import com.lobox.demo.repository.model.Crew;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
public class CrewStepConfiguration extends StepConfiguration<Crew> {

    private final CrewJpaRepository crewJpaRepository;

    @Value("${title.crew.file.path}")
    String fileName;

    public CrewStepConfiguration(CrewJpaRepository crewJpaRepository,
                                 JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager) {
        super(jobRepository, transactionManager);
        this.crewJpaRepository = crewJpaRepository;
    }

    @Bean(name = "crewMasterStep")
    public Step masterStep(JobRepository jobRepository) {
        return new StepBuilder("crewMasterStep", jobRepository)
                .partitioner("crewBasicSlaveStep", new FilePartitioner(fileName, 50))
                .step(slaveStep(reader(null, 0, 0), processor(), writer()))
                .partitionHandler(partitionHandler(slaveStep(reader(null, 0, 0), processor(), writer())))
                .build();
    }


    @Override
    @Bean(name = "crewReader")
    @StepScope
    protected FlatFileItemReader<Crew> reader(@Value("#{stepExecutionContext['filePath']}") String fileName,
                                                    @Value("#{stepExecutionContext['startLine']}") int startLine,
                                                    @Value("#{stepExecutionContext['endLine']}") int endLine) {
        return new FlatFileItemReaderBuilder<Crew>()
                .name("basicItemReader")
                .resource(new ClassPathResource(fileName))
                .linesToSkip(startLine== 0 ? 1:startLine)
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy() {
                    private int currentLine = startLine;

                    @Override
                    public boolean isEndOfRecord(String line) {
                        return currentLine++ <= endLine && super.isEndOfRecord(line);
                    }
                }).delimited().delimiter("\t")
                .strict(false)
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
    @Bean("crewSlaveStep")
    public Step slaveStep(ItemReader<Crew> reader,
                         ItemProcessor<Crew, Crew> processor,
                         ItemWriter<Crew> writer) {
        return new StepBuilder("crewStep", jobRepository)
                .<Crew, Crew>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

//    @Override
//    @Bean(name = "crewPartitionHandler")
//    public TaskExecutorPartitionHandler partitionHandler(Step slaveStep) {
//        partitionHandler.setStep(slaveStep);
//        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        partitionHandler.setGridSize(500); // Adjust for the number of files/threads
//        return partitionHandler;
//    }
}
