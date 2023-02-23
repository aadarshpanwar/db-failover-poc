//package com.example.demo;
//
//
//import com.zaxxer.hikari.HikariDataSource;
//import software.amazon.jdbc.ds.AwsWrapperDataSource;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Properties;
//
//public class HikariTest {
//
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "k2V05jx*$cx&";
//    private static final String DATABASE_NAME = "test";
//    private static final String ENDPOINT = "dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com";
//
//    public static void main(String[] args) throws SQLException {
//        try (HikariDataSource ds = new HikariDataSource()) {
//
//            // Configure the connection pool:
//            ds.setUsername(USER);
//            ds.setPassword(PASSWORD);
//
//            // Specify the underlying datasource for HikariCP:
//            ds.setDataSourceClassName(AwsWrapperDataSource.class.getName());
//
//            // Configure AwsWrapperDataSource:
//            ds.addDataSourceProperty("jdbcProtocol", "jdbc:postgresql:");
//            ds.addDataSourceProperty("databasePropertyName", "databaseName");
//            ds.addDataSourceProperty("portPropertyName", "portNumber");
//            ds.addDataSourceProperty("serverPropertyName", "serverName");
//
//            // Specify the driver-specific data source for AwsWrapperDataSource:
//            ds.addDataSourceProperty("targetDataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
//
//            // Configuring PGSimpleDataSource:
//            Properties targetDataSourceProps = new Properties();
//            targetDataSourceProps.setProperty("serverName", ENDPOINT);
//            targetDataSourceProps.setProperty("databaseName", DATABASE_NAME);
//            targetDataSourceProps.setProperty("portNumber", "5432");
//
//            ds.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);
//
//            // Attempt a connection:
//            try (final Connection conn = ds.getConnection();
//                 final Statement statement = conn.createStatement();
//                 final ResultSet rs = statement.executeQuery("SELECT * from person")) {
//                while (rs.next()) {
//                    System.out.println(rs.getString(1));
//                }
//            }
//        }
//    }
//}
