package ben.math.distribution.resources;

import ben.math.distribution.api.DistributionInput;
import ben.math.distribution.api.DistributionOutput;
import com.codahale.metrics.annotation.Timed;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.Validate;

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
        try {
            return new DistributionOutput(input, computeExponentialPdf(input));
        } catch (Throwable throwable) {
            throw new WebApplicationException(throwable.getMessage(), 400);
        }
    }

    @VisibleForTesting
    Double computeExponentialPdf(DistributionInput input) {
        Validate.notNull(input, "input cannot be null");
        final Double lambda = input.getLambda();
        final Double x = input.getX();
        return computeExponentialPdf(lambda, x);
    }

    @VisibleForTesting
    Double computeExponentialPdf(Double lambda, Double x) {
        Validate.isTrue(lambda > 0, "lambda should be greater than 0");
        Validate.isTrue(x >= 0, "x should be non-negative");
        Double pdf = lambda * exp(-lambda * x);
        if (pdf.equals(0.0)) {
            pdf = exp(log(lambda) - lambda * x);
        }
        return pdf;
    }
}