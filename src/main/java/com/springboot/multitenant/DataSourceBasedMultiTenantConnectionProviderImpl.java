package com.springboot.multitenant;

import com.springboot.model.TenantDetails;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    private static final String DEFAULT_TENANT_ID = "acrsmasterdb";
    @Autowired
    private DataSource defaultDS;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private ApplicationContext context;

    private Map<String, DataSource> map = new HashMap<>();

    boolean init = false;

    @PostConstruct
    public void load() {
        map.put(DEFAULT_TENANT_ID, defaultDS);
        map.put("tenant1", getTenantDatasource(new TenantDetails("tenant1", "jdbc:postgresql://localhost:5432/tenant1", "postgres", "password1")));
        map.put("tenant2", getTenantDatasource(new TenantDetails("tenant2", "jdbc:postgresql://localhost:5432/tenant2", "postgres", "password1")));
    }

    private DataSource getTenantDatasource(TenantDetails tenantDetails) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(tenantDetails.getJdbcUrl());
        dataSource.setUsername(tenantDetails.getUsername());
        dataSource.setPassword(tenantDetails.getPassword());

        return dataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return map.get(DEFAULT_TENANT_ID);
    }

    @Override
    protected DataSource selectDataSource(Object tenantIdentifier) {
        return map.get(tenantIdentifier) != null ? map.get(tenantIdentifier) : map.get(DEFAULT_TENANT_ID);
    }
}