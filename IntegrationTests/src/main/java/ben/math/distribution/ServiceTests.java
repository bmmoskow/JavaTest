package ben.math.distribution;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.mashape.unirest.http.HttpResponse;

/**
 * Created by valued on 3/1/18.
 */
public class ServiceTests {
    private static ObjectMapper mapper = new ObjectMapper();
    private static String servicePath;
    private static int servicePort;
    private static int serviceHealthPort;
    private static String distributionPdfUrl;
    private static String awsBucketName;
    private static String awsS3DataLocation;
    private static String awsS3DataLocationTemp;

    private static AmazonS3 s3 = new AmazonS3Client(new ProfileCredentialsProvider());;

    @BeforeClass
    @Parameters({"servicePath", "servicePort", "serviceHealthPort", "awsBucketName", "awsS3DataLocation", "awsS3DataLocationTemp"})
    public static void beforeClass(String servicePath, int servicePort, int serviceHealthPort,
                                   String awsBucketName, String awsS3DataLocation, String awsS3DataLocationTemp) throws UnirestException, InterruptedException {
        Unirest.setDefaultHeader("CONTENT-TYPE", "application/json");

        ServiceTests.servicePath = servicePath;
        ServiceTests.servicePort = servicePort;
        ServiceTests.serviceHealthPort = serviceHealthPort;
        ServiceTests.awsBucketName = awsBucketName;
        ServiceTests.awsS3DataLocation = awsS3DataLocation;
        ServiceTests.awsS3DataLocationTemp = awsS3DataLocationTemp;
        distributionPdfUrl = servicePath + ":" + servicePort + "/distribution/exponential/pdf";

        copyTestDataToTempLocation();
        checkServiceRunning();
    }

    private static void copyTestDataToTempLocation() {
        for (String sourceKey : listS3ObjectKeys(awsS3DataLocation)) {
            String destinationKey = awsS3DataLocationTemp + sourceKey.substring(awsS3DataLocation.length());
            s3.copyObject(awsBucketName, sourceKey, awsBucketName, destinationKey);
        }
    }

    private static List<String> listS3ObjectKeys(String awsS3DataLocation) {
        List<S3ObjectSummary> summaries = s3.listObjects(awsBucketName, awsS3DataLocation).getObjectSummaries();
        List<String> keys = new LinkedList();
        for (S3ObjectSummary summary : summaries) {
            keys.add(summary.getKey());
        }

        return keys;
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
        deleteTempDataLocation();
    }

    private static void deleteTempDataLocation() {
        for (String key : listS3ObjectKeys(awsS3DataLocationTemp)) {
            s3.deleteObject(awsBucketName, key);
        }
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

    @DataProvider(name = "pdfDataAws")
    public static Object[][] pdfDataProviderAws() throws IOException {

        List<String> keys = listS3ObjectKeys(awsS3DataLocationTemp);

        Object[][] pdfDataObject = new Object[keys.size()][3];
        int i = 0;
        for (String key : keys) {
            Map<String, Object> pdfData = mapper.readValue(s3ObjectToString(s3, key), HashMap.class);
            pdfDataObject[i][0] = pdfData.get("lambda");
            pdfDataObject[i][1] = pdfData.get("x");
            pdfDataObject[i][2] = pdfData.get("pdf");
            i++;
        }

        return pdfDataObject;
    }

    private static String s3ObjectToString(AmazonS3 s3, String key) throws IOException {
        S3Object s3object = s3.getObject(new GetObjectRequest(awsBucketName, key));
        BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }


    @Test(dataProvider = "pdfData")
    public void baseline(Double lambda, Double x, Double pdf) throws UnirestException, IOException {
        validatePostiveCase(lambda, x, pdf);
    }

    @Test(dataProvider = "pdfDataAws")
    public void baselineAws(Double lambda, Double x, Double pdf) throws UnirestException, IOException {
        validatePostiveCase(lambda, x, pdf);
    }

    @Test
    public void integerParameterInputs() throws UnirestException, IOException {
        validatePostiveCase(1, 0, 1.0);
    }

    private void validatePostiveCase(Object lambda, Object x, Double pdf) throws UnirestException, IOException {
        String jsonBody = formJsonBody(lambda, x);
        HttpResponse response = Unirest.post(distributionPdfUrl).body(jsonBody).asJson();

        Assert.assertEquals(response.getStatus(), 200);
        Map<String, Object> responseBody = mapper.readValue(response.getBody().toString(), HashMap.class);
        Map<String, Object> input = (HashMap)responseBody.get("input");
        Assert.assertEquals(Double.valueOf(input.get("lambda").toString()), Double.valueOf(lambda.toString()));
        Assert.assertEquals(Double.valueOf(input.get("x").toString()), Double.valueOf(x.toString()));
        Assert.assertEquals(Double.valueOf(responseBody.get("pdf").toString()), pdf);
    }

    private String formJsonBody(Object lambda, Object x) {
        return "{ \"lambda\" : " + lambda + ", \"x\" : " + x + " }";
    }

    @Test
    public void zeroLambda() throws UnirestException, IOException {
        runErrorTest(0.0, 1.0, "lambda should be greater than 0");
    }

    @Test
    public void negativeLambda() throws UnirestException, IOException {
        runErrorTest(-1.0, 1.0, "lambda should be greater than 0");
    }

    @Test
    public void missingLambda() throws UnirestException, IOException {
        runErrorTest("{ \"x\" : 1.0 }", 400, "lambda must be specified");
    }

    @Test
    public void negativeX() throws UnirestException, IOException {
        runErrorTest(1.0, -1.0, "x should be non-negative");
    }

    @Test
    public void missingX() throws UnirestException, IOException {
        runErrorTest("{ \"lambda\" : 1.0 }", 400, "x must be specified");
    }

    @Test
    public void nonDoubleLambda() throws UnirestException, IOException {
        runErrorTest(1.0, "foo", "Unable to process JSON");
    }

    @Test
    public void nonDoubleX() throws UnirestException, IOException {
        runErrorTest("foo", 1.0,"Unable to process JSON");
    }

    @Test
    public void malformedJson() throws UnirestException, IOException {
        runErrorTest("{", 400, "Unable to process JSON");
    }

    @Test
    public void extractBodyParamter() throws UnirestException, IOException {
        runErrorTest("{ \"lambda\" : 1.0, \"x\" : 1.0, \"foo\" : \"bar\" }", 400, "Unable to process JSON");
    }

//    @Test
//    public void nullJson() throws UnirestException, IOException {
//        runErrorTest(null, 400, "Unable to process JSON");
//    }

    @Test
    public void emptyJson() throws UnirestException, IOException {
        runErrorTest("", 400, "input cannot be null");
    }

    @Test
    public void whitespaceJson() throws UnirestException, IOException {
        runErrorTest(" ", 400, "input cannot be null");
    }

    private void runErrorTest(Object lambda, Object x, String errorMessage) throws UnirestException, IOException {
        String jsonBody = formJsonBody(lambda, x);
        runErrorTest(jsonBody, 400, errorMessage);
    }

    private void runErrorTest(String jsonBody, int httpStatus, String errorMessage) throws UnirestException, IOException {
        HttpResponse response = Unirest.post(distributionPdfUrl).body(jsonBody).asJson();

        Assert.assertEquals(response.getStatus(), httpStatus);
        Map<String, Object> responseBody = mapper.readValue(response.getBody().toString(), HashMap.class);
        Assert.assertEquals(responseBody.get("code"), httpStatus);
        Assert.assertEquals(responseBody.get("message"), errorMessage);
    }
}
