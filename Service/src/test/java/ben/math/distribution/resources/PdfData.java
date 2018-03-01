package ben.math.distribution.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by valued on 3/1/18.
 */
public class PdfData {
    @NotEmpty
    private List<PdfDataPoint> pdfDataPoints;

    @JsonProperty
    public List<PdfDataPoint> getPdfDataPoints() {
        return pdfDataPoints;
    }

    @JsonProperty
    public void setPdfDataPoints(List<PdfDataPoint> pdfDataPoints) {
        this.pdfDataPoints = pdfDataPoints;
    }

}
