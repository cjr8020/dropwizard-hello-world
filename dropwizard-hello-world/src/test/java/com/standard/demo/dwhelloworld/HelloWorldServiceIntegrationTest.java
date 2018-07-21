package com.standard.demo.dwhelloworld;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.standard.dev.test.base.BaseTest;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service integration test exercises the REST endpoints. It creates an DW service instance and
 * then exercises it in JUnit test cases. The DW instance is configured from a dedicated config.
 *
 * NOTE: some test cases rely on data that must exist in the database - this data is created and
 * torn down before/after each test case.
 */
public class HelloWorldServiceIntegrationTest extends BaseTest {

  private static final Logger log = LoggerFactory.getLogger(HelloWorldServiceIntegrationTest.class);

  /*
   * database.url defined in this file must match the one defined in test.properties for this module
   */
  private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("config/HelloWorldServiceIntegrationTest-config.yml");

  /* service client */
  private static Client client = null;

  /* creates our member-update service instance */
  @ClassRule
  public static final DropwizardAppRule<HelloWorldServiceConfiguration>
      DW_RULE = new DropwizardAppRule<>(HelloWorldService.class, CONFIG_PATH);


  @Before
  public void before() {
    client = ClientBuilder.newClient();
  }

  @After
  public void after() throws Exception {
    client.close();
  }


  @Test
  public void testVersionResourceEndpoint() {

    Response response = client.target(String.format("http://localhost:%d/version", DW_RULE.getLocalPort()))
        .request()
        .get();
    assertThat(response.getStatus(), equalTo(200));
  }
}
