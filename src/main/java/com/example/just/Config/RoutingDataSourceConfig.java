package com.example.just.Config;

import static com.example.just.Util.DbConstUtil.BASE_PACKAGES;
import static com.example.just.Util.DbConstUtil.DATA_SOURCE;
import static com.example.just.Util.DbConstUtil.ENTITY_MANAGER;
import static com.example.just.Util.DbConstUtil.ENTITY_MANAGER_FACTORY;
import static com.example.just.Util.DbConstUtil.HIBERNATE_DIALECT;
import static com.example.just.Util.DbConstUtil.MASTER_DATE_SOURCE;
import static com.example.just.Util.DbConstUtil.ROUTING_DATA_SOURCE;
import static com.example.just.Util.DbConstUtil.SLAVE_DATE_SOURCE;
import static com.example.just.Util.DbConstUtil.TRANSACTION_MANAGER;

import java.util.HashMap;
import java.util.Map;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.PlatformTransactionManager;

@EnableJpaRepositories(
        basePackages = BASE_PACKAGES,
        entityManagerFactoryRef = ENTITY_MANAGER_FACTORY, // EntityMangeFactory빈 설정
        transactionManagerRef = TRANSACTION_MANAGER // Tansaction 관리자 빈 설정
)
@Configuration
public class RoutingDataSourceConfig {

    @Bean(ROUTING_DATA_SOURCE)
    public DataSource routingDataSource( // 라우티할 데이터베이스 동적 설정
            @Qualifier(MASTER_DATE_SOURCE) final DataSource master,
            @Qualifier(SLAVE_DATE_SOURCE) final DataSource slave
    ) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.MASTER, master);
        dataSourceMap.put(DataSourceType.SLAVE, slave);

        routingDataSource.setTargetDataSources(dataSourceMap); // 마스터, 슬레이브 두개 타겟 설정
        routingDataSource.setDefaultTargetDataSource(master); // Defaul 기본 데이터 소스로 마스터 설정

        return routingDataSource;
    }

    @Bean(DATA_SOURCE)
    public DataSource dataSource(@Qualifier(ROUTING_DATA_SOURCE) DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
        // 지연 로딩 지원, DB사용시까지
    }

    @Bean(ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean( // JPA entityMange 생성
            @Qualifier(DATA_SOURCE) DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource); // 앞서 받은 datSource 설정
        entityManagerFactory.setPackagesToScan(BASE_PACKAGES); // 스캔할 패키지
        entityManagerFactory.setJpaVendorAdapter(this.jpaVendorAdapter());
        entityManagerFactory.setPersistenceUnitName(ENTITY_MANAGER); // EntityManger Name
        // Hibernate 속성을 설정하기 위해 별도의 Properties 객체를 사용합니다.
        entityManagerFactory.setJpaProperties(hibernateProperties());
        return entityManagerFactory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.format_sql", "false");
        return properties;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(false);
        adapter.setShowSql(true);

        adapter.setDatabasePlatform(HIBERNATE_DIALECT);

        return adapter;
    }

    @Bean(TRANSACTION_MANAGER)
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier(ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean emf
    ) {

        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf.getObject());

        return jpaTransactionManager;
    }

}