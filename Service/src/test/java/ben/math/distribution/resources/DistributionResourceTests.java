package ben.math.distribution.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by valued on 3/1/18.
 */
public class DistributionResourceTests {
    private static DistributionResource dr;

    @BeforeClass
    public static void beforeClass() {
        dr = new DistributionResource();
    }

    @DataProvider(name = "pdfData")
    public static Object[][] pdfDataProvider() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PdfData pdfData = mapper.readValue(new File("src/test/resources/pdfData.json"), PdfData.class);
        Object[][] pdfDataObject = new Object[pdfData.getPdfDataPoints().size()][3];
        int i = 0;
        for (PdfDataPoint pdfDataPoint : pdfData.getPdfDataPoints()) {
            pdfDataObject[i][0] = pdfDataPoint.getLambda();
            pdfDataObject[i][1] = pdfDataPoint.getX();
            pdfDataObject[i][2] = pdfDataPoint.getPdf();
            i++;
        }
        return pdfDataObject;
//        return new Object[][]{{0.0, 0.0, 0.0}, {1.0, 1.0, 0.36787944117144233}};
    }

    @Test(dataProvider = "pdfData")
    public void computeExponentialPdf(Double lambda, Double x, Double pdf) {
        Assert.assertEquals(dr.computeExponentialPdf(lambda, x), pdf);
    }
}
