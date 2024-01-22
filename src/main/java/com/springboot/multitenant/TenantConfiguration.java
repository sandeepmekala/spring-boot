//package com.springboot.multitenant;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class TenantConfiguration {
//
//    @Bean
//    public TenantRoutingDataSource routingDataSource(
//            @Qualifier("defaultDataSource") DataSource defaultDataSource,
//            @Qualifier("dataSources") Map<String, DataSource> dataSources) {
//        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
//        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
//        routingDataSource.setTargetDataSources(new HashMap<>(dataSources));
//        return routingDataSource;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            TenantRoutingDataSource routingDataSource,
//            JpaProperties jpaProperties,
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("entityManagerFactory") Map<String, LocalContainerEntityManagerFactoryBean> entityManagers) {
//        Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();
//
//        for (Map.Entry<String, LocalContainerEntityManagerFactoryBean> entry : entityManagers.entrySet()) {
//            String tenantId = entry.getKey();
//            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = entry.getValue();
//            entityManagerFactories.put(tenantId, entityManagerFactoryBean.getObject());
//        }
//        routingDataSource.set
//        return new MultiTenantEntityManagerFactory(routingDataSource, jpaProperties.getProperties());
//    }
//}
