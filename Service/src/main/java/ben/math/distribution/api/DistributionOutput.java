package ben.math.distribution.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class DistributionOutput {
    @NotEmpty
    private DistributionInput input;

    @NotEmpty
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