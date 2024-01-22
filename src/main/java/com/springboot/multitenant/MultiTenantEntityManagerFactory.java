//package com.springboot.multitenant;
//
//import com.springboot.model.TenantContext;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//public class MultiTenantEntityManagerFactory extends AbstractRoutingDataSource {
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TenantContext.getCurrentTenant();
//    }
//}
