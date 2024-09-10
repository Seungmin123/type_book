package com.muzlive.kitpage.kitpage.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import java.sql.Connection;
import java.util.function.Supplier;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomQueryDslConfig {

    @PersistenceContext
    EntityManager entityManager;

    private final DataSource dataSource;

    @Autowired
    public CustomQueryDslConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public JPAQueryFactory queryFactory(){
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public com.querydsl.sql.Configuration queryDslConfig() {
        SQLTemplates templates = MySQLTemplates.builder().build();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

    @Bean
    public SQLQueryFactory sqlQueryFactory() {
        Supplier<Connection> supplier = new SpringConnectionProvider(dataSource);
        return new SQLQueryFactory(queryDslConfig(), supplier);
    }
}
