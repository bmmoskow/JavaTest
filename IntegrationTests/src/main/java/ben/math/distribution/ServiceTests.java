package ben.math.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;

/**
 * Created by valued on 3/1/18.
 */
public class ServiceTests {
    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() {
        Unirest.setDefaultHeader("CONTENT-TYPE", "application/json");
    }

    @AfterClass
    public static void afterClass() {
        Unirest.clearDefaultHeaders();
    }

    @DataProvider(name = "pdfData")
    public static Object[][] pdfDataProvider() throws IOException {

        List<Object> pdfData = mapper.readValue(new File("src/main/resources/pdfData.json"), ArrayList.class);
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
    public void baseline(Double lambda, Double x, Double pdf) throws UnirestException, IOException {
        String jsonBody = "{ \"lambda\" : " + lambda + ", \"x\" : " + x + " }";
        HttpResponse response = Unirest.post("http://localhost:9000/distribution/exponential/pdf").body(jsonBody).asJson();

        Assert.assertEquals(response.getStatus(), 200);
        Map<String, Object> responseBody = mapper.readValue(response.getBody().toString(), HashMap.class);
        Map<String, Object> input = (HashMap)responseBody.get("input");
        Assert.assertEquals(Double.valueOf(input.get("lambda").toString()), lambda);
        Assert.assertEquals(Double.valueOf(input.get("x").toString()), x);
        Assert.assertEquals(Double.valueOf(responseBody.get("pdf").toString()), pdf);
    }
}
