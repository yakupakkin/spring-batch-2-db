package com.example.project.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.project.pojo.Car;
import com.example.project.service.CarItemProcessor;
import com.example.project.service.JobCompletionNotificationListener;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

	final JobBuilderFactory jobBuilderFactory;

	final StepBuilderFactory stepBuilderFactory;

	final JobCompletionNotificationListener listener;

	final JobLauncher jobLauncher;

	@Qualifier("primaryDataSource")
	final DataSource primaryDataSource;

	@Qualifier("secondaryDataSource")
	final DataSource secondaryDataSource;

	@Scheduled(cron = "0 */2 * * * *")
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
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Car, Car>chunk(100)
				.reader(reader())
                .processor(processor())
				.writer(writer())
                .build();
    }

	private static final String QUERY_FIND_CARS = "SELECT id,brand,model,color FROM car";

	@Bean
	public ItemReader<Car> reader() {
		return new JdbcCursorItemReaderBuilder<Car>().name("cursorItemReader").dataSource(primaryDataSource)
				.sql(QUERY_FIND_CARS).rowMapper(new BeanPropertyRowMapper<>(Car.class)).build();
	}

    @Bean
    public CarItemProcessor processor() {
        return new CarItemProcessor();
    }

    @Bean
	public JdbcBatchItemWriter<Car> writer() {
        return new JdbcBatchItemWriterBuilder<Car>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO CAR (id,brand, model, color) VALUES (:id,:brand, :model, :color)")
				.dataSource(secondaryDataSource)
                .build();
    }

}
