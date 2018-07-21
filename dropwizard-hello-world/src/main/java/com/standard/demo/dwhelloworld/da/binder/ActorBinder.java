package com.standard.demo.dwhelloworld.da.binder;

import com.standard.demo.dwhelloworld.da.entity.Actor;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;

import java.sql.Timestamp;

/**
 * Binders bind entity fields to sql statements
 */
public class ActorBinder<T extends Actor> implements Binder<Bind, T> {

  @Override
  public void bind(SQLStatement<?> sqlStatement, Bind bind, T entity) {
    final String transactionType = bind.value();

    sqlStatement.bind("actorId", entity.getId());
    sqlStatement.bind("username", entity.getUsername());
    sqlStatement.bind("resourcesRequested", entity.getResourcesRequested());

    sqlStatement.bind("recordVersion", entity.getRecordVersion());

    if (transactionType.equalsIgnoreCase("create")) {
      sqlStatement.bind("createdBy", entity.getCreatedBy());
      sqlStatement.bind("createdTimestamp", Timestamp.valueOf(entity.getCreatedTimestamp()));
    } else if (transactionType.equalsIgnoreCase("update")) {
      sqlStatement.bind("updatedBy", entity.getUpdatedBy());
      sqlStatement.bind("updatedTimestamp", Timestamp.valueOf(entity.getUpdatedTimestamp()));
    } else {
      throw new RuntimeException("unknown transaction type: " + transactionType);
    }

  }
}
