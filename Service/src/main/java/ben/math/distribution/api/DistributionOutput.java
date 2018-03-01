package ben.math.distribution.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributionOutput {
    private DistributionInput input;
    private Double result;

    public DistributionOutput() {
        // Jackson deserialization
    }

    public DistributionOutput(DistributionInput input, Double x) {
        this.input = input;
        this.result = x;
    }

    @JsonProperty
    public DistributionInput getInput() {
        return input;
    }

    @JsonProperty
    public Double getResult() {
        return result;
    }
}