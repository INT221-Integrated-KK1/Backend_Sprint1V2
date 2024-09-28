package com.example.int221integratedkk1_backend.Config;

import jakarta.persistence.EntityManagerFactory;
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

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "boardEntityManagerFactory",
        transactionManagerRef = "boardTransactionManager",
        basePackages = {"com.example.int221integratedkk1_backend.Repositories.Taskboard"}
)
public class TaskBoardConfig {
    @Primary
    @Bean(name = "boardDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "boardEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean TaskboardEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("boardDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.int221integratedkk1_backend.Entities.Taskboard")
                .persistenceUnit("board")
                .build();
    }

    @Primary
    @Bean(name = "boardTransactionManager")
    public PlatformTransactionManager TaskboardTransactionManager(
            @Qualifier("boardEntityManagerFactory") EntityManagerFactory TaskboardEntityManagerFactory
    ) {
        return new JpaTransactionManager(TaskboardEntityManagerFactory);
    }
}

