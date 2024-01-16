package com.authorisation.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public static DataSource createConnectionPool() {
        // The configuration object specifies behaviors for the connection pool.
        HikariConfig config = new HikariConfig();

        // Configure which instance and what database user to connect with.
        config.setJdbcUrl(String.format("jdbc:postgresql:///%s", "postgres"));
        config.setUsername("postgres"); // e.g. "root", _postgres"
        config.setPassword(System.getenv("DB_PASSWORD")); // e.g. "my-password"

        // The ipTypes argument can be used to specify a comma delimited list of preferred IP types
        // for connecting to a Cloud SQL instance. The argument ipTypes=PRIVATE will force the
        // SocketFactory to connect with an instance's associated private IP.
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");

        // Initialize the connection pool using the configuration object.
        return new HikariDataSource(config);
    }

}
