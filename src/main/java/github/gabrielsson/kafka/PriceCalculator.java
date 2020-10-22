package github.gabrielsson.kafka;

import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PriceCalculator {

    private static final double CONVERSION_RATE = 0.88;

    @Traced
    public double convert(int priceInUsd) {
        return priceInUsd * priceInUsd;
    }
}
