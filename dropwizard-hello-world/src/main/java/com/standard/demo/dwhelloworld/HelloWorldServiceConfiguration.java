package com.standard.demo.dwhelloworld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.standard.sfg.security.oauth.spring.OAuthConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Bean;

/**
 * Service configuration class.
 * It assembles all externalized environment specific parameters from the single service config file - config.yml
 */
@org.springframework.context.annotation.Configuration
class HelloWorldServiceConfiguration extends Configuration {

  @NotEmpty
  private String greeting = "Hello World";

  @JsonProperty
  @Bean(name = "greeting")
  public String getGreeting() {
    return greeting;
  }

  @JsonProperty
  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  @NotNull
  private OAuthConfiguration oauthConfiguration;

  public OAuthConfiguration getOauthConfiguration() {
    return oauthConfiguration;
  }

  @JsonProperty("oauth")
  public void setOauthConfiguration(OAuthConfiguration oauthConfiguration) {
    this.oauthConfiguration = oauthConfiguration;
  }

  @Valid
  @NotNull
  @JsonProperty("database")
  private DataSourceFactory database = new DataSourceFactory();

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("greeting", greeting)
        .add("oauth", oauthConfiguration)
        .add("database", database)
        .toString();
  }
}
