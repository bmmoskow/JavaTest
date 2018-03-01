package ben.math.distribution.resources;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by valued on 3/1/18.
 */
public class DistributionResourceTests {
    private static DistributionResource dr;

    @BeforeClass
    public static void beforeClass() {
        dr = new DistributionResource();
    }

    @Test
    public void computeExponentialPdf_baseline() {
        Assert.assertEquals(dr.computeExponentialPdf(0.0, 0.0), 0.0);
    }

    @Test
    public void computeExponentialPdf_1() {
        Assert.assertEquals(dr.computeExponentialPdf(1.0, 1.0), 0.36787944117144233);
    }
}
