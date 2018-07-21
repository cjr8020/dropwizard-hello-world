/*
 *  create test ACTOR records
 */

// -- jsmith
INSERT INTO ACTOR (
  ACTOR_ID,
  USERNAME,
  RESOURCES_REQUESTED,
  RECORD_VERSION,
  CREATED_BY,
  CREATED_TIMESTAMP
) VALUES (
  1,
  'jsmith',
  0,
  0,
  'UnitTest',
  SYSDATE
);

// -- jdoe
INSERT INTO ACTOR (
  ACTOR_ID,
  USERNAME,
  RESOURCES_REQUESTED,
  RECORD_VERSION,
  CREATED_BY,
  CREATED_TIMESTAMP
) VALUES (
  2,
  'jdoe',
  0,
  0,
  'UnitTest',
  SYSDATE
);