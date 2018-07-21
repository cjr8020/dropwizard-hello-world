package com.standard.demo.dwhelloworld.resources;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Service;

/**
 * This resource reports version and build timestamp for this service/application
 */
@Service
@Path("/version")
public class VersionResource {

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response returnVersionInfo() throws IOException {
    final String versionInfo;
    try (InputStream versionData = getClass().getClassLoader().getResourceAsStream("version.json")) {
      versionInfo = CharStreams.toString(new InputStreamReader(versionData, Charsets.UTF_8));
    }
    return Response.ok(versionInfo, MediaType.APPLICATION_JSON).build();
  }
}
