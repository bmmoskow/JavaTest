package ben.math.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by valued on 3/1/18.
 */
public class ServiceTests {
    @DataProvider(name = "pdfData")
    public static Object[][] pdfDataProvider() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<Object> pdfData = mapper.readValue(new File("src/test/resources/pdfData.json"), ArrayList.class);
        Object[][] pdfDataObject = new Object[pdfData.size()][3];
        for (int i = 0; i < pdfData.size(); i++) {
            Map<String, Object> pdfDataPoint1 = (HashMap)pdfData.get(0);
            pdfDataObject[i][0] = pdfDataPoint1.get("lambda");
            pdfDataObject[i][1] = pdfDataPoint1.get("x");
            pdfDataObject[i][2] = pdfDataPoint1.get("pdf");
        }

        return pdfDataObject;
    }

    @Test(dataProvider = "pdfData")
    public void computeExponentialPdf(Double lambda, Double x, Double pdf) {

    }
}
