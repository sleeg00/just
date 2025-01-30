package com.example.just.Config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {
    // AbstractRoungDataSource " Spring Boot는 DB Connection 담고 있는 DataSource존재
    // 그 중 하나...

    @Override
    protected Object determineCurrentLookupKey() {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {

            return DataSourceType.SLAVE;
        }

        return DataSourceType.MASTER;
    }

}