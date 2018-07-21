package com.standard.demo.dwhelloworld;

import com.palantir.config.crypto.EncryptedConfigValueBundle;
import com.standard.sfg.audit.feature.RequestAuditLogFeature;
import com.standard.sfg.spring.context.SpringContextLoaderListener;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Class containing the main method for the Service/Application
 */
public class HelloWorldService extends Application<HelloWorldServiceConfiguration> {

  private static final Logger log = LoggerFactory.getLogger(HelloWorldService.class);

  private static final String APPLICATION_NAME = "dropwizard-hello-world";

  public static void main(String[] args) throws Exception {
    new HelloWorldService().run(args);
  }

  @Override
  public String getName() {
    return APPLICATION_NAME;
  }

  /**
   * Dropwizard Application class' initialize method is where you enable capabilities
   * beyond those provided in the dropwizard core module.
   * @param bootstrap The pre-start application environment, containing everything required to bootstrap a DW  command
   */
  @Override
  public void initialize(Bootstrap<HelloWorldServiceConfiguration> bootstrap) {
    // add encrypted-config-value-bundle
    bootstrap.addBundle(new EncryptedConfigValueBundle());
  }

  /**
   * The run() method is where you create instances of your REST resources, HealthChecks,
   * providers, tasks, and anything else you need to make your service functional.
   * @param configuration the DW configuration defaults + what you declared in your service config file
   * @param environment the DW service operating environment container
   * @throws Exception runtime exception
   */
  @Override
  public void run(HelloWorldServiceConfiguration configuration, Environment environment)
      throws Exception {

    /*
     * Enable CORS headers
     */
    final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
    // configure CORS parameters
    cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
    cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
    cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
    // add URL mapping
    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


    // Flyway migration
    DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(
        dataSourceFactory.getUrl(),
        dataSourceFactory.getUser(),
        dataSourceFactory.getPassword()
    );
    flyway.migrate();


    // add log audit feature
    environment.jersey().register(RequestAuditLogFeature.class);


    /*
     * JDBI for persistence
     * This will automatically create
     *  - managed connection pool to the database
     *  - a healthcheck for connectivity to the database
     */
    final DBIFactory dwxFactory = new DBIFactory();
    final DBI demoDbDbi = dwxFactory.build(environment, configuration.getDataSourceFactory(), "DEMO_DB");


    /*
     * Create Spring context.
     * before we init the app context, we have to create a parent context
     * with all the config objects others rely on to get initialized.
     */
    AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
    AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

    parent.refresh();
    parent.registerShutdownHook();
    parent.start();
    ConfigurableListableBeanFactory beanFactory = parent.getBeanFactory();
    beanFactory.registerSingleton(configuration.getClass().getCanonicalName(), configuration);
    beanFactory.registerSingleton("demoDbDbi", demoDbDbi);

    //the real main app context has a link to the parent context
    ctx.setParent(parent);

    ctx.getEnvironment().getPropertySources().addLast(
        configuration.getOauthConfiguration().getOauthConfigurationPropertySource());

    ctx.scan("com.standard.sfg.audit");
    ctx.scan("com.standard.sfg.security");
    ctx.scan(this.getClass().getPackage().getName());

    ctx.refresh();
    ctx.registerShutdownHook();
    ctx.start();


    environment.servlets().addServletListeners(new SpringContextLoaderListener(ctx));
    FilterRegistration.Dynamic filterRegistration = environment.servlets()
        .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
    filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");


    /* **************************
     * Register REST resources
     ***************************/

    //resources
    Map<String, Object> resources = ctx.getBeansWithAnnotation(Path.class);
    for (Map.Entry<String, Object> entry : resources.entrySet()) {
      environment.jersey().register(entry.getValue());
    }

    //JAX-RS providers (classes annotated with @javax.ws.rs.ext.Provider)
    Map<String, Object> providers = ctx.getBeansWithAnnotation(Provider.class);
    for(Map.Entry<String,Object> entry : providers.entrySet()) {
      environment.jersey().register(entry.getValue());
    }


    log.info(" =============== PARENT CONTEXT ============");
    printManagedBeans(parent);
    log.info(" =============== CHILD CONTEXT ============");
    printManagedBeans(ctx);


  }

  private void printManagedBeans(ApplicationContext context) {
    String[] beans = context.getBeanDefinitionNames();
    Arrays.sort(beans);
    for (String bean : beans) {
      log.info("BEAN={}", bean);
    }
  }
}
