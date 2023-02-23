package com.example.demo;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class HikariTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "k2V05jx*$cx&";
    private static final String DATABASE_NAME = "test";
    private static final String ENDPOINT = "dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com";

    public static void main(String[] args) throws SQLException {

        PGSimpleDataSource pg = new PGSimpleDataSource();
        pg.setUser(USER);
        pg.setPassword(PASSWORD);
        pg.setURL("jdbc:postgresql://dbfailover-poc.cluster-ro-cdibd9suphjv.eu-west-1.rds.amazonaws.com:5432/test");
        // ds.setDataSourceClassName("software.aws.rds.jdbc.postgresql.Driver");
        //return ds
        try (HikariDataSource ds = connectionPoolDataSource(pg)) {

            // Configure the connection pool:
            ds.setUsername(USER);
            ds.setPassword(PASSWORD);


            // Attempt a connection:
            try (final Connection conn = ds.getConnection();
                 final Statement statement = conn.createStatement();
                 final ResultSet rs = statement.executeQuery("SELECT * from person")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        }
    }

    static DataSource readOnlyDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(USER);
        ds.setPassword(PASSWORD);
        ds.setURL(ENDPOINT);
        // ds.setDataSourceClassName("software.aws.rds.jdbc.postgresql.Driver");
        //return ds;
        return connectionPoolDataSource(ds);
    }

    static HikariConfig hikariConfig(DataSource dataSource) {
        HikariConfig hikariConfig = new HikariConfig();
        // hikariConfig.setMaximumPoolSize(maxPoolSize);
        // hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setDataSource(dataSource);
        hikariConfig.setPassword(PASSWORD);
        hikariConfig.setJdbcUrl("jdbc:postgresql:aws://dbfailover-poc.cluster-ro-cdibd9suphjv.eu-west-1.rds.amazonaws.com:5432/test");

        hikariConfig.setAutoCommit(false);
        hikariConfig.setDriverClassName("software.aws.rds.jdbc.postgresql.Driver");
        return hikariConfig;
    }

    static HikariDataSource connectionPoolDataSource(DataSource dataSource) {
        HikariDataSource source= new HikariDataSource(hikariConfig(dataSource));
        //source.setDriverClassName("software.aws.rds.jdbc.postgresql.Driver");
        return source;
    }
}
