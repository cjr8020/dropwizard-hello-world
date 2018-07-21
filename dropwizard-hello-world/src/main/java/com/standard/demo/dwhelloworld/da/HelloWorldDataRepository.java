package com.standard.demo.dwhelloworld.da;

import com.google.common.base.Stopwatch;

import com.standard.demo.dwhelloworld.da.entity.Actor;

import com.standard.sfg.audit.SfgAuditableFactory.SfgAuditor;
import com.standard.sfg.audit.context.ExecutionContextHolder;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;

/**
 * This class defines methods that execute multiple DAO methods in the same transaction.. or
 * different methods of the same DAO again within the same transaction.
 */
public class HelloWorldDataRepository {

  private static final Logger logger = LoggerFactory.getLogger(HelloWorldDataRepository.class);

  private DBI dbi;
  private SfgAuditor sfgAuditor;

  public HelloWorldDataRepository(final DBI dbi, final SfgAuditor sfgAuditor) {
    this.dbi = dbi;
    this.sfgAuditor = sfgAuditor;
  }

  public Actor updateActorDetails(final String username) {

    return dbi.inTransaction((handle, transactionStatus) -> {
      final Stopwatch sw = Stopwatch.createStarted();

      ActorDao actorDao = handle.attach(ActorDao.class);
      Actor actor = actorDao.readByUsername(username);

      if (actor == null) {
        actor = new Actor(
            username,
            ExecutionContextHolder.get().getActor(),
            LocalDateTime.now());
        actor.incrementNumberOfResourcesRequested(); // reflect this login
        actorDao.create(actor);
      } else {
        actor.incrementNumberOfResourcesRequested();
        actor.setUpdatedBy(ExecutionContextHolder.get().getActor());
        actor.setUpdatedTimestamp(LocalDateTime.now());
        int recordsUpdated = actorDao.update(actor); // note: recordVer is updated automatically by the update implementation
        checkState(
            recordsUpdated == 1,
            "actor={} record failed to update.",
            ExecutionContextHolder.get().getActor());
      }

      if (sw.isRunning()) {
        sw.stop();
      }
      if (logger.isInfoEnabled()) {
        logger.info("{}, updateUserDetails completed in {}ms",
            sfgAuditor.auditableLogMessage(ExecutionContextHolder.get()),
            sw.elapsed(TimeUnit.MICROSECONDS));
      }
      return actor;
    });
  }
}
