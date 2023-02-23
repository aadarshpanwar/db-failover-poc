package com.example.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.jdbc.ds.AwsWrapperDataSource;
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

//    @Bean
//    public DataSource mysqlDataSource() {
////        DriverManagerDataSource dataSource = new DriverManagerDataSource();
////        dataSource.setDriverClassName("software.amazon.jdbc.Driver");
////        dataSource.setUrl(primaryUrl);
////        dataSource.setUsername(username);
////        dataSource.setPassword(password);
//        HikariDataSource ds = new HikariDataSource();
//        ds.setExceptionOverrideClassName("software.amazon.jdbc.util.HikariCPSQLException");
//            // Configure the connection pool:
//            ds.setUsername(username);
//            ds.setPassword(password);
//          //  ds.setAutoCommit(false);
//            // Specify the underlying datasource for HikariCP:
//            ds.setDataSourceClassName(AwsWrapperDataSource.class.getName());
//
//            // Configure AwsWrapperDataSource:
//            ds.addDataSourceProperty("jdbcProtocol", "jdbc:postgresql:");
//            ds.addDataSourceProperty("databasePropertyName", "databaseName");
//            ds.addDataSourceProperty("portPropertyName", "portNumber");
//            ds.addDataSourceProperty("serverPropertyName", "serverName");
//            //ds.setJdbcUrl("jdbc:aws-wrapper:postgresql://dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com:5432/test");
//            // Specify the driver-specific data source for AwsWrapperDataSource:
//            ds.addDataSourceProperty("targetDataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
//
//            // Configuring PGSimpleDataSource:
//            Properties targetDataSourceProps = new Properties();
//            targetDataSourceProps.setProperty("serverName", "dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com");
//            targetDataSourceProps.setProperty("databaseName", "test");
//            targetDataSourceProps.setProperty("portNumber", "5432");
//            targetDataSourceProps.setProperty("wrapperPlugins", "failover");
//
//        ds.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);
//        return ds;
//    }

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