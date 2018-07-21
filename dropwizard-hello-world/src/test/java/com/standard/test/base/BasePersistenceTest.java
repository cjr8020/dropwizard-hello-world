package com.standard.test.base;

import com.standard.dev.test.base.BaseTest;
import com.standard.test.rules.JdbiTestResourceRule;

import org.junit.Rule;

/**
 * Tests concerned with persistence should extend this class.
 * This class provides test cases with access to the DBI handle allowing them to
 * initialize DAO objects under test. E.g.:
 *
 *    jdbiResource.getHandle();
 *
 * Created by craiskin on 7/7/2015.
 */
public class BasePersistenceTest extends BaseTest {
  @Rule
  public JdbiTestResourceRule jdbiResource = new JdbiTestResourceRule();
}
