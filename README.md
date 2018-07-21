# dropwizard-hello-world

Repo | Status
--- | ---
ITSD/Release-Candidate | [![Build Status](https://jenkins.standard.com:8443/buildStatus/icon?job=ITSD/dropwizard-hello-world/release-candidate)](https://jenkins.standard.com:8443/job/ITSD/job/dropwizard-hello-world/job/release-candidate/)
DEV/develop | [![Build Status](https://jenkins.standard.com:8443/buildStatus/icon?job=ITSD/dropwizard-hello-world/develop)](https://jenkins.standard.com:8443/job/ITSD/job/dropwizard-hello-world/job/develop/)


sample Dropwizard v1.3.4 REST service supporting deployment and infrastructure services testing

The service demonstrates the following aspects:

1. TLS based service endpoint utilizing a keystore
1. logging
1. management port (JMX) TBD
1. management endpoint with health checks and metrics
1. database backend with an exposed health check
1. Basic Authentication

*Note*: this application is very simple and does **not** represent the organization of a typical software application project structure that would look more like this:

```
web-user-context-parent
 |
 +- id3-oracle-da
 |
 +- web-user-context-core
 |
 +- web-user-context-service
 |
 +- web-user-context-test-resources
```

## Reverse Proxy

The service endpoint is fronted by a WebSEAL reverse proxy.

Use any existing TAM LDAP test account to access these resources.



## hello-world resource

resource URI:

```
https://intportalweb.standard.com/dw-test-app/hello-world
```

The service will respond with `Hello World` as well as the authenticated userid and the number of times this accessed the resources at this endpoint. 

sample response

```
HTTP/1.1 200 OK
Date: Thu, 15 Jun 2017 22:49:16 GMT
x-transaction-id: soapui-transid-1234abcd
actor: soapui-test
Content-Type: application/json
Vary: Accept-Encoding
Content-Length: 71

{
   "message": "Hello World",
   "username": "soapui-test",
   "resourceRequests": 5
}
```

## hello-world-caps resource

resource URI:

```
https://intportalweb.standard.com/dw-test-app/hello-world-caps
```

The service will respond with `HELLO WORLD` as well as the authenticated userid and the number of times this accessed the resources at this endpoint. 

sample response

```
HTTP/1.1 200 OK
Date: Thu, 15 Jun 2017 22:48:46 GMT
x-transaction-id: soapui-transid-1234abcd
actor: soapui-test
Content-Type: application/json
Vary: Accept-Encoding
Content-Length: 71

{
   "message": "HELLO WORLD",
   "username": "soapui-test",
   "resourceRequests": 4
}
```


## Service logging

The service writes its application to a shared filesystem available to Windows clients at

```
\\sfglogs\pojologs\SIC\dropwizard-hello-world\pdxlmicni001\dropwizard-hello-world.log
```
## Management endpoint

when viewed in the browser

```
http://pdxlmicni001
```

displays operational menu:

```
Operational Menu

Metrics
Ping
Threads
Healthcheck
CPU Profile
CPU Contention
```

### Metrics

#### gauges

metrics for any managed resource utilized by the application.
`managed resource` is a feature of the Dropwizard framework.

For instance, this service utilizes a DB backend managed resource `DEMO_DB`.
`DEMO_DB` gauge displays the following attributes:

```
    "io.dropwizard.db.ManagedPooledDataSource.DEMO_DB.active" : {
      "value" : 0
    },
    "io.dropwizard.db.ManagedPooledDataSource.DEMO_DB.idle" : {
      "value" : 8
    },
    "io.dropwizard.db.ManagedPooledDataSource.DEMO_DB.size" : {
      "value" : 8
    },
    "io.dropwizard.db.ManagedPooledDataSource.DEMO_DB.waiting" : {
      "value" : 0
    }
```

#### JVM metrics

Enable programmatic collection of JVM statistics as opposed to a human-driven approach.
JVM metrics can be collected and graphed with a graphing tool like [Graphite](https://graphiteapp.org).  Performance thresholds can be configured to trigger proactive notifications prompting remedial action.


```
    "jvm.attribute.vendor" : {
      "value" : "Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 25.121-b13 (1.8)"
    },
    .
    .
    .
```

### Ping 

A simple `ping-pong` health check for the service endpoint to validate that it is up is available `out-of-the-box`.

### Threads

Detailed information on all processing threads, their status.

### health checks

Out of the box, the framework provides one health check on thread deadlocks.

In addition, this service exposes a health check for its external dependency - DB backend:

```
{
  "DEMO_DB" : {
    "healthy" : true
  },
  "deadlocks" : {
    "healthy" : true
  }
}
```

When the health check admin endpoint is exercised, the health check probes `DEMO_DB` with a pre-configured DB query and displays `health` if the query returns the expected result.
 
### CPU Profile and CPU Contention
 
Allows you download `pprof` files viewable with CPU profiling tools.  
 
