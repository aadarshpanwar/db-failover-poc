package com.example.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
//import software.amazon.jdbc.ds.AwsWrapperDataSource;

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

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

//    @Value("${spring.datasource.hikari.maximumPoolSize}")
//    private int maxPoolSize;
//
//    @Value("${spring.datasource.hikari.minimumIdle}")
//    private int minimumIdle;

    @Bean
    public DataSource readWriteDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(username);
        ds.setPassword(password);
        ds.setURL(primaryUrl);
      //  ds.setDriverClassName("software.aws.rds.jdbc.postgresql.Driver");
       // ds.setDataSourceClassName("software.aws.rds.jdbc.postgresql.Driver");
        // Configure AwsWrapperDataSource:
//        ds.addDataSourceProperty("jdbcProtocol", "jdbc:postgresql:");
//        ds.addDataSourceProperty("databasePropertyName", "databaseName");
//        ds.addDataSourceProperty("portPropertyName", "portNumber");
//        ds.addDataSourceProperty("serverPropertyName", "serverName");
//
//        // Specify the driver-specific data source for AwsWrapperDataSource:
//        ds.addDataSourceProperty("targetDataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
//
//        // Configuring PGSimpleDataSource:
//        Properties targetDataSourceProps = new Properties();
//        targetDataSourceProps.setProperty("serverName", "dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com");
//        targetDataSourceProps.setProperty("databaseName", "test");
//        targetDataSourceProps.setProperty("portNumber", "5432");
//        targetDataSourceProps.setProperty("wrapperPlugins", "failover");
//
//        targetDataSourceProps.setProperty("failoverTimeoutMs", "30000");
//        targetDataSourceProps.setProperty("failoverWriterReconnectIntervalMs", "2000");
//        targetDataSourceProps.setProperty("failoverReaderConnectTimeoutMs", "10000");
//        targetDataSourceProps.setProperty("failoverClusterTopologyRefreshRateMs", "2000");


      //  ds.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);
       // return ds;
        return connectionPoolDataSource(ds);
    }

    @Bean
    public DataSource readOnlyDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(username);
        ds.setPassword(password);
        ds.setURL(replicaUrl);
       // ds.setDataSourceClassName("software.aws.rds.jdbc.postgresql.Driver");
        //return ds;
        return connectionPoolDataSource(ds);
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
       // hikariConfig.setMaximumPoolSize(maxPoolSize);
       // hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setDataSource(dataSource);
        hikariConfig.setPassword(password);

        hikariConfig.setAutoCommit(false);
        hikariConfig.setDriverClassName("software.aws.rds.jdbc.postgresql.Driver");
        return hikariConfig;
    }

    protected HikariDataSource connectionPoolDataSource(DataSource dataSource) {
    HikariDataSource source= new HikariDataSource(hikariConfig(dataSource));
        //source.setDriverClassName("software.aws.rds.jdbc.postgresql.Driver");
        return source;
    }
}

 class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
                DataSourceType.READ_ONLY :
                DataSourceType.READ_WRITE;
    }

 }

 enum DataSourceType {
    READ_WRITE,
    READ_ONLY
}