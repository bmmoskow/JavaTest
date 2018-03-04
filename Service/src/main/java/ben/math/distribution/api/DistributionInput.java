package ben.math.distribution.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class DistributionInput {
    @NotEmpty
    private Double lambda;

    @NotEmpty
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