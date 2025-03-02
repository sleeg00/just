//package com.example.just.Config.DataSoruce;
//
//
//import static com.example.just.Util.DbConstUtil.BASE_PACKAGES;
//import static com.example.just.Util.DbConstUtil.DATA_SOURCE;
//import static com.example.just.Util.DbConstUtil.HIBERNATE_DIALECT;
//
//import com.example.just.Config.DataSoruce.ReplicationDataSourceProperties.Read;
//import com.zaxxer.hikari.HikariDataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
//import org.springframework.orm.jpa.JpaTransactionManager;
//
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@RequiredArgsConstructor
//public class ReplicationDataSourceConfiguration {
//    private final ReplicationDataSourceProperties replicationDataSourceProperties;
//    private final JpaProperties jpaProperties;
//    @Bean
//    public DataSource routingDataSource() {
//        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
//
//        ReplicationDataSourceProperties.Write write = replicationDataSourceProperties.getWrite();
//        DataSource writeDataSource = createDataSource(write.getUrl());
//
//        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put(write.getName(), writeDataSource);
//
//        List<Read> reads = replicationDataSourceProperties.getReads();
//        for (ReplicationDataSourceProperties.Read read : reads) {
//            dataSourceMap.put(read.getName(), createDataSource(read.getUrl()));
//        }
//
//        replicationRoutingDataSource.setDefaultTargetDataSource(writeDataSource);
//        replicationRoutingDataSource.setTargetDataSources(dataSourceMap);
//        replicationRoutingDataSource.afterPropertiesSet();
//
//        return new LazyConnectionDataSourceProxy(replicationRoutingDataSource);
//    }
//
//    private DataSource createDataSource(String url) {
//        HikariDataSource hikariDataSource = new HikariDataSource();
//        hikariDataSource.setDriverClassName(replicationDataSourceProperties.getDriverClassName());
//        hikariDataSource.setUsername(replicationDataSourceProperties.getUsername());
//        hikariDataSource.setPassword(replicationDataSourceProperties.getPassword());
//        hikariDataSource.setJdbcUrl(url);
//
//        return hikariDataSource;
//    }
//
//
//
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(// JPA entityMange 생성
//    ) {
//        EntityManagerFactoryBuilder entityManagerFactoryBuilder = createEntityManagerFactoryBuilder();
//        return entityManagerFactoryBuilder.dataSource(routingDataSource()).packages(BASE_PACKAGES).build();
//    }
//
//    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder() {
//        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
//
//        adapter.setGenerateDdl(false);
//        adapter.setShowSql(false);
//        adapter.setDatabasePlatform(HIBERNATE_DIALECT);
//
//        jpaProperties.setShowSql(false);
//        jpaProperties.setGenerateDdl(false);
//
//        return new EntityManagerFactoryBuilder(adapter, jpaProperties.getProperties(), null);
//    }
//}