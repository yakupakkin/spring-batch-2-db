package com.example.project.configuration;

import javax.sql.DataSource;

import com.example.project.pojo.Car;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.project.service.JobCompletionNotificationListener;
import com.example.project.service.CarItemProcessor;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    JobLauncher jobLauncher;
    @Value("${file.input}")
    private String fileInput;
    DataSource dataSource;
    @Autowired
    JobCompletionNotificationListener listener;

    @Bean
    public FlatFileItemReader<Car> reader() {
        BeanWrapperFieldSetMapper<Car> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(Car.class);
        return new FlatFileItemReaderBuilder<Car>().name("carItemReader")
                .resource(new ClassPathResource(fileInput)).delimited().names("brand", "model", "color")
                .fieldSetMapper(beanWrapperFieldSetMapper).build();
    }

    @Bean
    public JdbcBatchItemWriter<Car> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Car>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO CAR (brand, model, color) VALUES (:brand, :model, :color)")
                .dataSource(dataSource).build();
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(importUserJob(), params);
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end().build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Car, Car>chunk(100).reader(reader()).processor(processor())
                .writer(writer(dataSource)).build();
    }

    @Bean
    public CarItemProcessor processor() {
        return new CarItemProcessor();
    }
}
