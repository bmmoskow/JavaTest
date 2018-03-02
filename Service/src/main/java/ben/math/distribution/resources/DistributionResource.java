package ben.math.distribution.resources;

import ben.math.distribution.api.DistributionInput;
import ben.math.distribution.api.DistributionOutput;
import com.codahale.metrics.annotation.Timed;
import com.google.common.annotations.VisibleForTesting;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static java.lang.Math.exp;
import static java.lang.Math.log;

@Path("/distribution")
@Produces(MediaType.APPLICATION_JSON)
public class DistributionResource
{
    @POST
    @Path("/exponential/pdf")
    @Timed
    public DistributionOutput returnPdf(DistributionInput input) {
        return new DistributionOutput(input, computeExponentialPdf(input));
    }

    @VisibleForTesting
    Double computeExponentialPdf(DistributionInput input) {
        final Double lambda = input.getLambda();
        final Double x = input.getX();
        return computeExponentialPdf(lambda, x);
    }

    protected Double computeExponentialPdf(Double lambda, Double x) {
        Double pdf = lambda * exp(-lambda * x);
        if (pdf.equals(0.0)) {
            pdf = exp(log(lambda) - lambda * x);
        }
        return pdf;
    }
}