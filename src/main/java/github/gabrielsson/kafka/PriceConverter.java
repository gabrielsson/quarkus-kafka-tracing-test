package github.gabrielsson.kafka;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * A bean consuming data from the "prices" Kafka topic and applying some conversion.
 * The result is pushed to the "my-data-stream" stream which is an in-memory stream.
 */
@ApplicationScoped
public class PriceConverter {

    @Inject
    PriceCalculator priceCalculator;

    @Inject
    Tracer tracer;

    @Incoming("prices")
    public void process(int priceInUsd) {
        priceCalculator.convert(priceInUsd);
    }

    @Incoming("prices-traced")
    @Outgoing("my-data-stream")
    @Broadcast
    public double processTraced(Message<Integer> priceInUsd) {
        var record = (IncomingKafkaRecord) priceInUsd;
        var spanContext = TracingKafkaUtils.extractSpanContext(record.getHeaders(), tracer);
        try(var scope = tracer.buildSpan("processTraced")
            .asChildOf(spanContext)
            .startActive(true)) {

            return priceCalculator.convert(priceInUsd.getPayload());
        }
    }

}