package com.springboot.multitenant;

import com.springboot.model.TenantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.*;

@Configuration
public class TenantDataSourceProvider {

    private static final String DEFAULT_TENANT_ID = "acrsmasterdb";

    @Autowired
    private JpaProperties jpaProperties;

    @Bean
    public Map<String, DataSource> dataSources() {
        // Fetch tenant information from API and create DataSources dynamically
        Map<String, DataSource> dataSources = new HashMap<>();

        // Example: Creating DataSources for tenants
        List<TenantDetails> tenantDetailsList = fetchTenantDetailsFromApi();

        for (TenantDetails tenantDetails : tenantDetailsList) {
            DataSource dataSource = createDataSourceForTenant(tenantDetails);
            dataSources.put(tenantDetails.getTenantId(), dataSource);
        }

        return dataSources;
    }

    private List<TenantDetails> fetchTenantDetailsFromApi() {
        List<TenantDetails> tenants = new ArrayList<>();
        tenants.add(new TenantDetails("tenant1", "jdbc:postgresql://localhost:5432/tenant1", "postgres", "password1"));
        tenants.add(new TenantDetails("tenant2", "jdbc:postgresql://localhost:5432/tenant2", "postgres", "password1"));

        return tenants;
    }

    private DataSource createDataSourceForTenant(TenantDetails tenantDetails) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(tenantDetails.getJdbcUrl());
        dataSource.setUsername(tenantDetails.getUsername());
        dataSource.setPassword(tenantDetails.getPassword());
        return dataSource;
    }

//    @Bean
//    public Map<String, LocalContainerEntityManagerFactoryBean> entityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("defaultDataSource") DataSource defaultDataSource,
//            @Qualifier("dataSources") Map<String, DataSource> dataSources) {
//        Map<String, LocalContainerEntityManagerFactoryBean> entityManagers = new HashMap<>();
//
//        for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
//            String tenantId = entry.getKey();
//            DataSource dataSource = entry.getValue();
//
//            LocalContainerEntityManagerFactoryBean entityManagerFactory =
//                    builder
//                            .dataSource(dataSource)
//                            .packages("com.springboot")
//                            .properties(jpaProperties.getProperties())
//                            .persistenceUnit(tenantId)
//                            .build();
//
//            entityManagers.put(tenantId, entityManagerFactory);
//        }
//
//        return entityManagers;
//    }
}

