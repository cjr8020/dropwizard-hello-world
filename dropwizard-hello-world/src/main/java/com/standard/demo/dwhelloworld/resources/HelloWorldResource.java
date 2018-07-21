package com.standard.demo.dwhelloworld.resources;

import static com.standard.demo.dwhelloworld.PvConstants.CUSTOM_HTTP_HEADER_TRANSACTION_ID;

import com.google.common.base.Stopwatch;
import com.standard.demo.dwhelloworld.da.HelloWorldDataRepository;
import com.standard.demo.dwhelloworld.da.entity.Actor;
import com.standard.demo.dwhelloworld.representation.Greeting;
import com.standard.sfg.audit.SfgAuditableFactory.SfgAuditor;
import com.standard.sfg.audit.annotations.Audited;
import com.standard.sfg.audit.context.ExecutionContextHolder;
import com.standard.sfg.security.oauth.SfgJwt;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Hello World Resource class - exposes hello world resources
 * Note: @Path required on resource class with Jersey 2.5.x
 */
@Service
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

  private static final Logger logger = LoggerFactory.getLogger(HelloWorldResource.class);

  private final String greetingMessage;
  private final DBI demoDbDbi;
  private final SfgAuditor sfgAuditor;

  @Autowired
  public HelloWorldResource(
      final String greetingMessage,
      final DBI demoDbDbi,
      final SfgAuditor sfgAuditor
  ) {
    this.greetingMessage = greetingMessage;
    this.demoDbDbi = demoDbDbi;
    this.sfgAuditor = sfgAuditor;
  }

  /**
   * Says hello.
   *
   * @param transactionId string
   * @return Response
   */
  @Path("hello-world")
  @GET
  @Audited
  public Response sayHello(
      @HeaderParam(CUSTOM_HTTP_HEADER_TRANSACTION_ID) String transactionId
  ) {

    Response response;
    final Stopwatch sw = Stopwatch.createStarted();

    try {

      SfgJwt sfgJwt = new SfgJwt(SecurityContextHolder.getContext().getAuthentication());

      /*
       * -------------------------------------------------------------------------------------------
       * audit info setup
       * -------------------------------------------------------------------------------------------
       */
      final String operationName = "sayHello";
      String serviceName = getClass().getPackage().getImplementationTitle();
      String serviceVersion = getClass().getPackage().getImplementationVersion();
      ExecutionContextHolder.set(sfgAuditor.createAndValidateExecutionContext(
          transactionId, // sfgGtid
          operationName,
          sfgJwt,
          serviceName,
          serviceVersion));

      /*
       * -------------------------------------------------------------------------------------------
       * actual business logic execution
       * -------------------------------------------------------------------------------------------
       */

      HelloWorldDataRepository dataRepository
          = new HelloWorldDataRepository(demoDbDbi, sfgAuditor);
      Actor actor = dataRepository.updateActorDetails(sfgJwt.getSub());
      Greeting greeting
          = new Greeting(greetingMessage, actor.getUsername(), actor.getResourcesRequested());
      GenericEntity<Greeting> responseEntity = new GenericEntity<Greeting>(greeting) {
      };
      response = Response.ok(responseEntity).build();
    } catch (Throwable throwable) {
      logger.error(
          "{}, operation=sayHello failed: {}",
          sfgAuditor.auditableLogMessage(ExecutionContextHolder.get()),
          throwable);
      response = Response.status(Response.Status.BAD_REQUEST)
          .entity(Response.Status.BAD_REQUEST.getReasonPhrase())
          .build();
    } finally {

      // stop the stopwatch after all business logic processing is complete.
      sw.stop();

      if (logger.isInfoEnabled()) {
        logger.info("{}, duration={}ms",
            sfgAuditor.auditableLogMessage(ExecutionContextHolder.get()),
            sw.elapsed(TimeUnit.MILLISECONDS));
      }
      /*
       * -------------------------------------------------------------------------------------------
       * !!!! absolutely MANDATORY cleanup !!!!
       * clear ExecutionContextHolder before completing a RequestMapping method.
       * -------------------------------------------------------------------------------------------
       */
      ExecutionContextHolder.clear();
    }

    response.getHeaders().add(CUSTOM_HTTP_HEADER_TRANSACTION_ID, transactionId);

    return response;
  }

  /**
   * Says hello in caps.
   *
   * @param transactionId string
   * @return Response
   */
  @Path("hello-world-caps")
  @GET
  @Audited
  public Response sayHelloInCaps(
      @HeaderParam(CUSTOM_HTTP_HEADER_TRANSACTION_ID) String transactionId
  ) {

    Response response;
    final Stopwatch sw = Stopwatch.createStarted();

    try {
      SfgJwt sfgJwt = new SfgJwt(SecurityContextHolder.getContext().getAuthentication());

      /*
       * -------------------------------------------------------------------------------------------
       * audit info setup
       * -------------------------------------------------------------------------------------------
       */
      final String operationName = "sayHelloInCaps";
      String serviceName = getClass().getPackage().getImplementationTitle();
      String serviceVersion = getClass().getPackage().getImplementationVersion();
      ExecutionContextHolder.set(sfgAuditor.createAndValidateExecutionContext(
          transactionId, // sfgGtid
          operationName,
          sfgJwt,
          serviceName,
          serviceVersion));


      /*
       * -------------------------------------------------------------------------------------------
       * actual business logic execution
       * -------------------------------------------------------------------------------------------
       */

      HelloWorldDataRepository dataRepository
          = new HelloWorldDataRepository(demoDbDbi, sfgAuditor);
      Actor actor = dataRepository.updateActorDetails(sfgJwt.getSub());
      Greeting greeting = new Greeting(
          greetingMessage.toUpperCase(),
          actor.getUsername(),
          actor.getResourcesRequested());
      GenericEntity<Greeting> responseEntity = new GenericEntity<Greeting>(greeting) {
      };
      response = Response.ok(responseEntity).build();
    } catch (Throwable throwable) {
      logger.error(
          "{}, operation=sayHello failed: {}",
          sfgAuditor.auditableLogMessage(ExecutionContextHolder.get()),
          throwable);
      response = Response.status(Response.Status.BAD_REQUEST)
          .entity(Response.Status.BAD_REQUEST.getReasonPhrase())
          .build();
    } finally {
      // stop the stopwatch after all business logic processing is complete.
      sw.stop();

      if (logger.isInfoEnabled()) {
        logger.info("{}, duration={}ms",
            sfgAuditor.auditableLogMessage(ExecutionContextHolder.get()),
            sw.elapsed(TimeUnit.MILLISECONDS));
      }
      /*
       * -------------------------------------------------------------------------------------------
       * !!!! absolutely MANDATORY cleanup !!!!
       * clear ExecutionContextHolder before completing a RequestMapping method.
       * -------------------------------------------------------------------------------------------
       */
      ExecutionContextHolder.clear();
    }

    response.getHeaders().add(CUSTOM_HTTP_HEADER_TRANSACTION_ID, transactionId);

    return response;
  }

}
