package com.authorisation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("dev")
public class DevDataSourceConfig {

    @Bean
    public DataSource createDevConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://localhost/%s", "postgres"));
        config.setUsername("postgres");
        config.setPassword("postgres");

        return new HikariDataSource(config);
    }
}
