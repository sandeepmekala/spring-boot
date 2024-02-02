package com.springboot.multitenant;

import jakarta.persistence.EntityManagerFactory;
import org.eclipse.persistence.config.CommitOrderType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudConfiguration {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
//        return new HibernateJpaVendorAdapter();
        return new EclipseLinkJpaVendorAdapter();
    }
    @Bean
    @Primary
    public MultitenantDatasource routingDataSource(
            @Qualifier("defaultDatasource") DataSource defaultDatasource,
            @Qualifier("dataSources") Map<String, DataSource> dataSources) {
        MultitenantDatasource routingDataSource = new MultitenantDatasource();
        routingDataSource.setDefaultTargetDataSource(defaultDatasource);
        routingDataSource.setTargetDataSources(new HashMap<>(dataSources));
        return routingDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaVendorAdapter adapter,
                                                                       DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.springboot.*");
        emf.setJpaVendorAdapter(adapter);
        emf.setJpaPropertyMap(getJpaProperties());
        return emf;
    }

//    @Bean
//    @Primary
//    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//        return getJpaTransactionManager(emf);
//    }
//
//    protected JpaTransactionManager getJpaTransactionManager(EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
    private Map<String,String> getJpaProperties() {
        Map<String, String> map = new HashMap<>();
        map.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
        map.put(PersistenceUnitProperties.WEAVING, "static");
        map.put(PersistenceUnitProperties.LOGGING_LEVEL, "ALL");
        map.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        map.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");
        map.put(PersistenceUnitProperties.JDBC_DRIVER, "org.postgresql.Driver");
        map.put(PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "2");
        map.put(PersistenceUnitProperties.CONNECTION_POOL_MAX, "1000");
        map.put(PersistenceUnitProperties.CONNECTION_POOL_MIN, "1000");
        map.put(PersistenceUnitProperties.CONNECTION_POOL_WAIT, "300000");
        map.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_COMMIT_ORDER, CommitOrderType.Changes);
        return map;
    }
}
