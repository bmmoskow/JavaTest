package ben.math.distribution.resources;

import ben.math.distribution.api.DistributionInput;
import ben.math.distribution.api.DistributionOutput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Object> pdfData = mapper.readValue(new File("src/test/resources/pdfData.json"), ArrayList.class);
        Object[][] pdfDataObject = new Object[pdfData.size()][3];
        for (int i = 0; i < pdfData.size(); i++) {
            Map<String, Object> pdfDataPoint1 = (HashMap)pdfData.get(i);
            pdfDataObject[i][0] = pdfDataPoint1.get("lambda");
            pdfDataObject[i][1] = pdfDataPoint1.get("x");
            pdfDataObject[i][2] = pdfDataPoint1.get("pdf");
        }

        return pdfDataObject;
    }

    @Test(dataProvider = "pdfData")
    public void computeExponentialPdf(Double lambda, Double x, Double pdf) {
        Assert.assertEquals(dr.computeExponentialPdf(lambda, x), pdf);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "lambda should be greater than 0")
    public void computeExponentialPdfZeroLambda() {
        dr.computeExponentialPdf(0.0, 1.0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "lambda should be greater than 0")
    public void computeExponentialPdfNegativeLambda() {
        dr.computeExponentialPdf(-1.0, 1.0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "x should be non-negative")
    public void computeExponentialPdfNegativeX() {
        dr.computeExponentialPdf(1.0, -1.0);
    }

    @Test(dataProvider = "pdfData")
    public void computeExponentialPdf1(Double lambda, Double x, Double pdf) {
        Assert.assertEquals(dr.computeExponentialPdf(new DistributionInput(lambda, x)), pdf);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "lambda should be greater than 0")
    public void computeExponentialPdfZeroLambda1() {
        dr.computeExponentialPdf(new DistributionInput(0.0, 1.0));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "lambda should be greater than 0")
    public void computeExponentialPdfNegativeLambda1() {
        dr.computeExponentialPdf(new DistributionInput(-1.0, 1.0));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "x should be non-negative")
    public void computeExponentialPdfNegativeX1() {
        dr.computeExponentialPdf(new DistributionInput(1.0, -1.0));
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "input cannot be null")
    public void computeExponentialPdfNull() {
        dr.computeExponentialPdf(null);
    }

    @Test(dataProvider = "pdfData")
    public void returnPdf(Double lambda, Double x, Double pdf) {
        DistributionOutput distributionOutput = dr.returnPdf(new DistributionInput(lambda, x));
        Assert.assertEquals(distributionOutput.getInput().getLambda(), lambda);
        Assert.assertEquals(distributionOutput.getInput().getX(), x);
        Assert.assertEquals(distributionOutput.getPdf(), pdf);
    }

    @Test(expectedExceptions = WebApplicationException.class, expectedExceptionsMessageRegExp = "lambda should be greater than 0")
    public void returnPdfZeroLambda() {
        dr.returnPdf(new DistributionInput(0.0, 1.0));
    }

    @Test(expectedExceptions = WebApplicationException.class, expectedExceptionsMessageRegExp = "lambda should be greater than 0")
    public void returnPdfNegativeLambda() {
        dr.returnPdf(new DistributionInput(-1.0, 1.0));
    }

    @Test(expectedExceptions = WebApplicationException.class, expectedExceptionsMessageRegExp = "x should be non-negative")
    public void returnPdfNegativeX() {
        dr.returnPdf(new DistributionInput(1.0, -1.0));
    }

    @Test(expectedExceptions = WebApplicationException.class, expectedExceptionsMessageRegExp = "input cannot be null")
    public void returnPdfNull() {
        dr.returnPdf(null);
    }
}
