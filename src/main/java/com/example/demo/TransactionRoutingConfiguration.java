package com.example.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import software.amazon.jdbc.ds.AwsWrapperDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class TransactionRoutingConfiguration {

    @Value("${jdbc.url.primary}")
    private String primaryUrl;

    @Value("${jdbc.url.replica}")
    private String replicaUrl;

    @Value("${jdbc.url.schema}")
    private String schema;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private int maxPoolSize = 5;

    private int minimumIdle = 1;

    @Bean
    public DataSource readWriteDataSource() {
        return new HikariDataSource(hikariConfig(primaryUrl));
    }

    @Bean
    public DataSource readOnlyDataSource() {
        return new HikariDataSource(hikariConfig(replicaUrl));
    }

    @Primary
    @DependsOn({"readWriteDataSource", "readOnlyDataSource"})
    @Bean
    public TransactionRoutingDataSource actualDataSource() {
        TransactionRoutingDataSource routingDataSource = new TransactionRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.READ_WRITE, readWriteDataSource());
        dataSourceMap.put(DataSourceType.READ_ONLY, readOnlyDataSource());

        routingDataSource.setTargetDataSources(dataSourceMap);
        return routingDataSource;
    }

    protected HikariConfig hikariConfig(String endpoint) {
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
        hikariConfig.addDataSourceProperty("targetDataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        Properties targetDataSourceProps = new Properties();
        targetDataSourceProps.setProperty("currentSchema", schema);
        targetDataSourceProps.setProperty("serverName", endpoint);
        targetDataSourceProps.setProperty("databaseName", "test");
        targetDataSourceProps.setProperty("portNumber", "5432");
        //failover plugin specific config
        targetDataSourceProps.setProperty("wrapperPlugins", "failover");
        targetDataSourceProps.setProperty("failoverTimeoutMs", "30000");
        targetDataSourceProps.setProperty("failoverWriterReconnectIntervalMs", "2000");
        targetDataSourceProps.setProperty("failoverReaderConnectTimeoutMs", "10000");
        targetDataSourceProps.setProperty("failoverClusterTopologyRefreshRateMs", "2000");
        hikariConfig.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);
        return hikariConfig;
    }

    protected HikariDataSource connectionPoolDataSource(String serverUrl) {
        return new HikariDataSource(hikariConfig(serverUrl));
    }

    @Bean
    public BeanPostProcessor dialectProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof HibernateJpaVendorAdapter) {
                    ((HibernateJpaVendorAdapter) bean).getJpaDialect().setPrepareConnection(false);
                }
                return bean;
            }
        };
    }
}
