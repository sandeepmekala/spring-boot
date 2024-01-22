//package com.springboot.multitenant;
//
//import org.hibernate.cfg.Environment;
//import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
//import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class HibernateConfig {
//    @Autowired
//    public JpaProperties jpaProperties;
//
//    @Bean
//    public JpaVendorAdapter jpaVendorAdapter() {
//        return new HibernateJpaVendorAdapter();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaVendorAdapter adapter,
//            DataSource dataSource
//    ) {
//
//        Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
//        jpaPropertiesMap.put(Environment.FORMAT_SQL, true);
//        jpaPropertiesMap.put(Environment.SHOW_SQL, true);
//
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.springboot.*");
//        em.setJpaVendorAdapter(adapter);
//        em.setJpaPropertyMap(jpaPropertiesMap);
//        return em;
//    }
//}