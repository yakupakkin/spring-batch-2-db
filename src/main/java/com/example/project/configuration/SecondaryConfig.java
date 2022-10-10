package com.example.project.configuration;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "secondaryEntityManagerFactoryBean", transactionManagerRef = "secondaryTransactionManager",
		basePackages = { "com.example.project.repository" }
)
public class SecondaryConfig {

	@Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource test2Datasource() {
        return DataSourceBuilder.create().build();
    }

	@Bean(name = "secondaryEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean
			secondaryEntityManagerFactoryBean(EntityManagerFactoryBuilder builder,
					@Qualifier("secondaryDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
				.packages("com.example.project.pojo")
				.persistenceUnit("CAR")
                .build();
    }

	@Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager transactionManager(
			@Qualifier("secondaryEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}