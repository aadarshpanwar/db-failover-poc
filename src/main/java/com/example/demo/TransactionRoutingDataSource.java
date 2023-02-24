package com.example.demo;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/*
This is used to check if current transaction is read only
*/
public class TransactionRoutingDataSource extends AbstractRoutingDataSource {
    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        System.out.println("Current transaction readonly:"+ TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
                DataSourceType.READ_ONLY :
                DataSourceType.READ_WRITE;
    }
}

enum  DataSourceType {
    READ_WRITE,
    READ_ONLY
}

