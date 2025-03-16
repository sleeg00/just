package com.example.just.Util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DbConstUtil {
    public static final String PROFILE_PROD = "prod";
    public static final String BASE_PACKAGES = "com.example.just";
    public static final String MASTER_DATE_SOURCE = "masterDataSource";
    public static final String SLAVE_DATE_SOURCE = "slaveDataSource";
    public static final String MASTER_PREFIX = "spring.datasource.master.hikari";
    public static final String SLAVE_PREFIX = "spring.datasource.slave.hikari";
    public static final String ROUTING_DATA_SOURCE = "routingDataSource";
    public static final String DATA_SOURCE = "dataSource";
    public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
    public static final String TRANSACTION_MANAGER = "transactionManager";
    public static final String ENTITY_MANAGER = "entityManager";
    public static final String HIBERNATE_DIALECT = "org.hibernate.dialect.MySQL8Dialect";
}