package com.example.just.Config;

import static com.example.just.Util.DbConstUtil.MASTER_DATE_SOURCE;
import static com.example.just.Util.DbConstUtil.MASTER_PREFIX;
import static com.example.just.Util.DbConstUtil.SLAVE_DATE_SOURCE;
import static com.example.just.Util.DbConstUtil.SLAVE_PREFIX;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DataSourceConfig { // DataSourceBean생성 Config

    @Primary // 우선적 주입
    @Bean(MASTER_DATE_SOURCE) // Bean 등록
    @ConfigurationProperties(prefix = MASTER_PREFIX) // MASTER_PREFIX로 된 yml 설정 읽어오기
    public DataSource masterDataSource() {
        return DataSourceBuilder // Spring DataSource 생성 클래스
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(SLAVE_DATE_SOURCE)
    @ConfigurationProperties(prefix = SLAVE_PREFIX) // SLAVE_PREFIX로 된 yml 설정 읽어오기
    public DataSource replicaDataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

}