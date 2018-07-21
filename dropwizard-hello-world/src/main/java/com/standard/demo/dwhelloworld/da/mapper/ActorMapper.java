package com.standard.demo.dwhelloworld.da.mapper;

import com.standard.demo.dwhelloworld.da.entity.Actor;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps ResultSet to Entity
 */
public class ActorMapper implements ResultSetMapper<Actor> {

  @Override
  public Actor map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException {

    Actor actor = new Actor();

    actor.setId(resultSet.getLong("ACTOR_ID"));
    actor.setUsername(resultSet.getString("USERNAME"));
    actor.setResourcesRequested(resultSet.getInt("RESOURCES_REQUESTED"));

    actor.setRecordVersion(resultSet.getInt("RECORD_VERSION"));

    actor.setCreatedBy(resultSet.getString("CREATED_BY"));
    actor.setCreatedTimestamp(
        resultSet.getTimestamp("CREATED_TIMESTAMP") == null ? null : resultSet.getTimestamp("CREATED_TIMESTAMP").toLocalDateTime()
    );
    actor.setUpdatedBy(resultSet.getString("UPDATED_BY"));
    actor.setUpdatedTimestamp(
        resultSet.getTimestamp("UPDATED_TIMESTAMP") == null ? null : resultSet.getTimestamp("UPDATED_TIMESTAMP").toLocalDateTime()
    );

    return actor;
  }
}
