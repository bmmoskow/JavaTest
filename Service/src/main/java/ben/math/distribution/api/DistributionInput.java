package ben.math.distribution.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributionInput {
    private Double lambda;
    private Double x;

    public DistributionInput() {
        // Jackson deserialization
    }

    public DistributionInput(Double lambda, Double x) {
        this.lambda = lambda;
        this.x = x;
    }

    @JsonProperty
    public Double getLambda() {
        return lambda;
    }

    @JsonProperty
    public Double getX() {
        return x;
    }
}