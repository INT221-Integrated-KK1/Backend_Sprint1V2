package com.example.int221integratedkk1_backend.Config;

import jakarta.persistence.EntityManagerFactory;
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

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "shareDatabaseEntityManagerFactory",
        transactionManagerRef = "shareDatabaseTransactionManager",
        basePackages = {"com.example.int221integratedkk1_backend.Repositories.Account"}
)

public class ShareDatabaseConfig {

    @Bean(name = "shareDatabaseDataSource")
    @ConfigurationProperties(prefix = "share-data.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "shareDatabaseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shareDatabaseEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("shareDatabaseDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.int221integratedkk1_backend.Entities.Account")
                .persistenceUnit("shareDatabase")
                .build();
    }

    @Bean(name = "shareDatabaseTransactionManager")
    public PlatformTransactionManager shareDatabaseTransactionManager(
            @Qualifier("shareDatabaseEntityManagerFactory") EntityManagerFactory shareDatabaseEntityManagerFactory
    ) {
        return new JpaTransactionManager(shareDatabaseEntityManagerFactory);
    }
}
