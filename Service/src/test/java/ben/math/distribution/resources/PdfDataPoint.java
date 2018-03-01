package ben.math.distribution.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by valued on 3/1/18.
 */
public class PdfDataPoint {
    @NotEmpty
    private Double lambda;

    @NotEmpty
    private Double x;

    @NotEmpty
    private Double pdf;

    @JsonProperty
    public Double getLambda() {
        return lambda;
    }

    @JsonProperty
    public void setLambda(Double lambda) {
        this.lambda = lambda;
    }

    @JsonProperty
    public Double getX() {
        return x;
    }

    @JsonProperty
    public void setX(Double x) {
        this.x = x;
    }

    @JsonProperty
    public Double getPdf() {
        return pdf;
    }

    @JsonProperty
    public void setPdf(Double pdf) {
        this.pdf = pdf;
    }
}
