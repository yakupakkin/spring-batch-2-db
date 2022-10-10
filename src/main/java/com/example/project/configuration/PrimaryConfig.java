package com.example.project.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "primaryEntityManagerFactoryBean", transactionManagerRef = "primaryTransactionManager",
		basePackages = { "com.example.project.repository" }
)
public class PrimaryConfig {

	@Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource test1Datasource() {
        return DataSourceBuilder.create().build();
    }

	@Bean(name = "primaryEntityManagerFactoryBean")
    @Primary
    public LocalContainerEntityManagerFactoryBean
			primaryEntityManagerFactoryBean(EntityManagerFactoryBuilder builder,
					@Qualifier("primaryDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
				.packages("com.example.project.pojo").persistenceUnit("CAR")
                .build();
    }

	@Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(
			@Qualifier("primaryEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
