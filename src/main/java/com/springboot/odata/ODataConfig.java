//package com.springboot.odata;
//
//import org.apache.olingo.odata2.api.ODataServiceFactory;
//import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
//import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContextFactory;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.inject.Produces;
//
//@ApplicationScoped
//public class ODataConfig {
//
//    @Produces
//    @ApplicationScoped
//    public ODataJPAContext oDataJPAContext() {
//        return ODataJPAContextFactory.create();
//    }
//
//    @Produces
//    @ApplicationScoped
//    public ODataServiceFactory odataServiceFactory(ODataJPAContext oDataJPAContext) {
//        return ODataJPAServiceFactory.createFactory(oDataJPAContext);
//    }
//}
//
