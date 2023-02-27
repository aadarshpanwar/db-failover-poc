package com.example.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.jdbc.ds.AwsWrapperDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class TransactionRoutingConfiguration {

    @Value("${jdbc.url.primary-endpoint}")
    private String primaryEndpoint;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${jdbc.url.primary-port}")
    private String port;

    @Value("${jdbc.url.pimary-databasename}")
    private String dbName;

    @Value(("${jdbc.url.primary-schema}"))
    private String schema;
    private int maxPoolSize = 5;

    private int minimumIdle = 1;

    @Bean
    public DataSource dataSource() {
        return connectionPoolDataSource(primaryEndpoint);
    }

    protected HikariDataSource connectionPoolDataSource(String serverUrl) {
        return new HikariDataSource(hikariConfig(serverUrl));
    }

    protected HikariConfig hikariConfig(String primaryEndpoint) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setAutoCommit(false);
        hikariConfig.setDataSourceClassName(AwsWrapperDataSource.class.getName());
        hikariConfig.addDataSourceProperty("jdbcProtocol", "jdbc:postgresql:");
        hikariConfig.addDataSourceProperty("databasePropertyName", "databaseName");
        hikariConfig.addDataSourceProperty("portPropertyName", "portNumber");
        hikariConfig.addDataSourceProperty("serverPropertyName", "serverName");
        hikariConfig.setJdbcUrl("jdbc:aws-wrapper:postgresql://dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com:5432/test?currentSchema=test2");
        hikariConfig.addDataSourceProperty("targetDataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");

        Properties targetDataSourceProps = new Properties();
        targetDataSourceProps.setProperty("currentSchema", schema);
        targetDataSourceProps.setProperty("serverName", primaryEndpoint);
        targetDataSourceProps.setProperty("databaseName", dbName);
        targetDataSourceProps.setProperty("portNumber", port);
        //failover plugin specific config
        targetDataSourceProps.setProperty("wrapperPlugins", "failover");
        targetDataSourceProps.setProperty("failoverTimeoutMs", "30000");
        targetDataSourceProps.setProperty("failoverWriterReconnectIntervalMs", "2000");
        targetDataSourceProps.setProperty("failoverReaderConnectTimeoutMs", "10000");
        targetDataSourceProps.setProperty("failoverClusterTopologyRefreshRateMs", "2000");
        hikariConfig.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);
        return hikariConfig;
    }
}