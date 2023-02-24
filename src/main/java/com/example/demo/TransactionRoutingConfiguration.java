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

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TransactionRoutingConfiguration {

    @Value("${jdbc.url.primary}")
    private String primaryUrl;

    @Value("${jdbc.url.replica}")
    private String replicaUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private int maxPoolSize = 5;

    private int minimumIdle = 1;

    @Bean
    public DataSource readWriteDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(primaryUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return connectionPoolDataSource(dataSource);
    }

    @Bean
    public DataSource readOnlyDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(replicaUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return connectionPoolDataSource(dataSource);
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

    protected HikariConfig hikariConfig(DataSource dataSource) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setDataSource(dataSource);
        hikariConfig.setAutoCommit(false);
        return hikariConfig;
    }

    protected HikariDataSource connectionPoolDataSource(DataSource dataSource) {
        return new HikariDataSource(hikariConfig(dataSource));
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
