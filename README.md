# Reproducer sample using Spring Boot Api Gateway

I created this sample to report an error observed OpenTelemetry auto-instrumentation agent.
The spring-boot application is always run with jvm options `-javaagent:opentelemetry-javaagent.jar -Dotel.traces.exporter=jaeger -Dotel.metrics.exporter=none -Dotel.exporter.jaeger.endpoint=http://localhost:14250`  
To reproduce the observed error I use the sample in `http://localhost:8080/controller/backendmap`.

see https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/5186 

Starting the sample:

* start [jaeger](https://www.jaegertracing.io/docs/1.30/getting-started/) locally
* copy opentelemetry agent jar to file `opentelemetry-javaagent.jar`, e.g. version [v1.10.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.10.0) or [v1.9.2](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.9.2)
* run spring boot with opentelemetry agent injected with `./gradlew -Pprofile=agent bootRun`

## Behavior since v1.10.0

Two traces are created, although I would only expect one.

First trace for incoming web request on `/controller/backendmap`.

```
unknown_service:java /controller/backendmap
    unknown_service:java ApiGatewayController.backendmap
    unknown_service:java HTTP GET
        unknown_service:java /controller/pojo
            unknown_service:java ApiGatewayController.getPojo
```

Second trace created in Mono operation `<mono>.flatMap(it -> requestMojo());` in `ApiGatewayController#backendMap`

```         
unknown_service:java HTTP GET
    unknown_service:java /controller/mojo
        unknown_service:java ApiGatewayController.getMojo
```


## Expected behavior until agent v1.9.2

This is the expected behavior. Both outgoing http-client (`/controller/pojo` and `/controller/mojo`) calls are 
stitched to the initiating incoming webrequest `/contoller/backendmap`.

```
unknown_service:java /controller/backendmap
    unknown_service:java ApiGatewayController.backendmap
    unknown_service:java HTTP GET
        unknown_service:java /controller/pojo
            unknown_service:java ApiGatewayController.getPojo
    unknown_service:java HTTP GET
        unknown_service:java /controller/mojo
            unknown_service:java ApiGatewayController.getMojo
```

