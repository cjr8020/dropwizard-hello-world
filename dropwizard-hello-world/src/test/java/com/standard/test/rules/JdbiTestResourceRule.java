package com.standard.test.rules;

import com.codahale.metrics.MetricRegistry;

import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.Properties;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;

/**
 * All Persistence tests should use this rule.
 * You could create a BasePersistenceTest with this rule, and all of you Persistence
 * test cases will extend BasePersistenceTest..
 *
 * Purpose of this Rule:
 * 1. create a test DB for persistence testing
 * 2. provide JDBI resource from which a DB connection (or handle) is obtained.
 *
 * Created by craiskin on 6/24/2015.
 */
public class JdbiTestResourceRule extends ExternalResource {

  private static final Properties testProperties = System.getProperties();

  private DBI dbi;
  private Handle handle;
  private Flyway flyway;

  public Handle getHandle() {
    return this.handle;
  }

  public DBI getDbi() {
    return this.dbi;
  }

  @Override
  protected void before() throws Throwable {
    Environment environment = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null);
    dbi = new DBIFactory().build(environment, getDataSourceFactory(), "test");
    handle = dbi.open();
    // execute test DB migration
    migrateDatabase();
  }

  @Override
  protected void after() {
    handle.close();
    // drop all DB objects to clean up after the test
    flyway.clean();
  }

  private void migrateDatabase() {

    flyway = new Flyway();
    flyway.setDataSource(getDataSourceFactory().build(new MetricRegistry(), "test-datasource"));
    flyway.migrate();
  }

  private DataSourceFactory getDataSourceFactory() {
    DataSourceFactory dataSourceFactory = new DataSourceFactory();
    dataSourceFactory.setDriverClass(testProperties.getProperty("jdbc.driver"));
    dataSourceFactory.setUrl(testProperties.getProperty("jdbc.url"));
    dataSourceFactory.setUser(testProperties.getProperty("jdbc.username"));
    dataSourceFactory.setPassword(testProperties.getProperty("jdbc.password"));
    return dataSourceFactory;
  }

}
