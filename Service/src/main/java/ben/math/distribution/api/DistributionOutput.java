package ben.math.distribution.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributionOutput {
    private DistributionInput input;
    private Double pdf;

    public DistributionOutput() {
        // Jackson deserialization
    }

    public DistributionOutput(DistributionInput input, Double pdf) {
        this.input = input;
        this.pdf = pdf;
    }

    @JsonProperty
    public DistributionInput getInput() {
        return input;
    }

    @JsonProperty
    public Double getPdf() {
        return pdf;
    }
}