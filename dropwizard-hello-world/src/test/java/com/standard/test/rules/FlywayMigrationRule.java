package com.standard.test.rules;

import com.codahale.metrics.MetricRegistry;

import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;

import java.util.Properties;

import io.dropwizard.db.DataSourceFactory;

/**
 * This org.junit.rules.ExternalResource rule invokes Flyway-managed database migration for your unit test.
 */
public class FlywayMigrationRule extends ExternalResource {

  private Flyway flyway;

  @Override
  protected void before() {
    // load test properties
    final Properties testProperties = System.getProperties();
    // create data source
    DataSourceFactory dataSourceFactory = new DataSourceFactory();
    dataSourceFactory.setDriverClass(testProperties.getProperty("jdbc.driver"));
    dataSourceFactory.setUrl(testProperties.getProperty("jdbc.url"));
    dataSourceFactory.setUser(testProperties.getProperty("jdbc.username"));
    dataSourceFactory.setPassword(testProperties.getProperty("jdbc.password"));

    flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.build(new MetricRegistry(), "test-datasource"));
    flyway.migrate();
  }

  @Override
  protected void after() {
    flyway.clean();
  }
}
