# quarkus-kafka-tracing-test project

This project showcases what we have to do to get tracing to work with `@Traced` annotation

See the Zulip thread for reference: https://quarkusio.zulipchat.com/#narrow/stream/187038-dev/topic/OpenTracing.20Kafka.20intereptors

The aim is to show what is necessary to do to trace projects using `smallrye-reactive-messaging-kafka` 

Using `io.opentracing.contrib.kafka.TracingConsumerInterceptor` together with `smallrye-reactive-messaging-kafka`
did not actually work as I expected. Inside the processing function with `@Incoming annotation the injected `
tracer does not actually have an active trace, nor does MDC have any variables set from the interceptor. The tracing 
interceptor merely closes the span so any future use of tracer within the processing function is not a child of the
FROM_prices span.

## Running the application in dev mode

In one terminal window start kafka
```
$ docker-compose up
```
In one terminal window start the jaeger-all-in-one container
```
docker run -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 jaegertracing/all-in-one:latest
```

Then start the application
```
$ ./mvnw quarkus:dev
```


## Result

Navigate to Jaeger UI http://localhost:16686 

### Problem
the span of operation `FROM_prices` only contains two spans
The actual processing of the prices are on their own.

The span of operation `FROM_prices-traced` contains all four spans 
as the scope is extracted from the headers manually. 