package com.authorisation.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("docker")
public class DevDockerDataSourceConfig {

    // Create connection to local postgres docker database if running the auth service in docker
    @Bean
    public DataSource createDevConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://postgres/%s", "postgres"));
        config.setUsername("postgres");
        config.setPassword("postgres");

        return new HikariDataSource(config);
    }
}
