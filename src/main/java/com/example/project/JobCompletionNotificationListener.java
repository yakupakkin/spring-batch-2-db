package com.example.project;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			/*
			 * String query = "SELECT brand, model, color FROM car";
			 * jdbcTemplate.query(query, (rs, row) -> new Car(rs.getString(1),
			 * rs.getString(2), rs.getString(3))) .forEach(car ->
			 * log.info("Found < {} > in the database.", car));
			 */
		}
	}
}
