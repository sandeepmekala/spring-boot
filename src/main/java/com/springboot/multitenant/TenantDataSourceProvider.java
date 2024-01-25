package com.springboot.multitenant;

import com.springboot.model.TenantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TenantDataSourceProvider {

    private static final String DEFAULT_TENANT_ID = "acrsmasterdb";

    @Autowired
    private JpaProperties jpaProperties;

    @Bean(name = "defaultDatasource")
    public DataSource defaultDatasource() {
        return createDataSourceForTenant(new TenantDetails("acrsmasterdb", "jdbc:postgresql://localhost:5432/acrsmasterdb", "postgres", "password1"));
    }

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
}

