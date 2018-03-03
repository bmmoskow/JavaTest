package ben.math.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.testng.Assert;
import org.testng.annotations.*;

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
    private static String servicePath;
    private static int servicePort;
    private static int serviceHealthPort;

    @BeforeClass
    @Parameters({"servicePath", "servicePort", "serviceHealthPort"})
    public static void beforeClass(String servicePath, int servicePort, int serviceHealthPort) throws UnirestException, InterruptedException {
        Unirest.setDefaultHeader("CONTENT-TYPE", "application/json");

        ServiceTests.servicePath = servicePath;
        ServiceTests.servicePort = servicePort;
        ServiceTests.serviceHealthPort = serviceHealthPort;

        checkServiceRunning();
    }

    private static void checkServiceRunning() throws UnirestException, InterruptedException {
        int maxTries = 30;
        int nTries = 0;

        while (nTries++ < maxTries) {
            try {
                HttpResponse response = Unirest.get(servicePath + ":" + serviceHealthPort + "/ping").asString();
                if (response.getStatus() == 200 && response.getBody().toString().equals("pong\n")) {
                    System.out.println("service confirmed to be running");
                    return;
                }
            } catch (Throwable throwable) {
                // do nothing
            }
            System.out.println("service not yet running, try " + nTries + " out of " + maxTries);
            Thread.sleep(1000);
        }

        Assert.fail("failed to get successful health check on the service in " + nTries + "attempts");
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
            Map<String, Object> pdfDataPoint1 = (HashMap)pdfData.get(i);
            pdfDataObject[i][0] = pdfDataPoint1.get("lambda");
            pdfDataObject[i][1] = pdfDataPoint1.get("x");
            pdfDataObject[i][2] = pdfDataPoint1.get("pdf");
        }

        return pdfDataObject;
    }

    @Test(dataProvider = "pdfData")
    public void baseline(Double lambda, Double x, Double pdf) throws UnirestException, IOException {
        String jsonBody = "{ \"lambda\" : " + lambda + ", \"x\" : " + x + " }";
        HttpResponse response = Unirest.post(servicePath + ":" + servicePort + "/distribution/exponential/pdf").body(jsonBody).asJson();

        Assert.assertEquals(response.getStatus(), 200);
        Map<String, Object> responseBody = mapper.readValue(response.getBody().toString(), HashMap.class);
        Map<String, Object> input = (HashMap)responseBody.get("input");
        Assert.assertEquals(Double.valueOf(input.get("lambda").toString()), lambda);
        Assert.assertEquals(Double.valueOf(input.get("x").toString()), x);
        Assert.assertEquals(Double.valueOf(responseBody.get("pdf").toString()), pdf);
    }
}
