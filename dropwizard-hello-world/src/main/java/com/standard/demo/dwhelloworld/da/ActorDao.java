package com.standard.demo.dwhelloworld.da;

import com.standard.demo.dwhelloworld.da.binder.ActorBinder;
import com.standard.demo.dwhelloworld.da.entity.Actor;
import com.standard.demo.dwhelloworld.da.mapper.ActorMapper;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterContainerMapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import io.dropwizard.jdbi.ImmutableSetContainerFactory;

/**
 * Actor DAO
 */
@RegisterMapper(ActorMapper.class)
@RegisterContainerMapper(ImmutableSetContainerFactory.class)
@UseStringTemplate3StatementLocator("/jdbi/sql/templates/ActorDao.sql.stg")
public interface ActorDao {

  @SqlUpdate
  @GetGeneratedKeys
  Long create(@Bind(value = "create", binder = ActorBinder.class) Actor actor);

  @SqlQuery
  Actor read(@Bind("actorId") Long actorId);

  @SqlQuery
  Actor readByUsername(@Bind("username") String username);

  @SqlUpdate
  int update(@Bind(value = "update", binder = ActorBinder.class) Actor actor);

}
