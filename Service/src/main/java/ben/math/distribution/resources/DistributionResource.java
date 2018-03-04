package ben.math.distribution.resources;

import ben.math.distribution.api.DistributionInput;
import ben.math.distribution.api.DistributionOutput;
import com.codahale.metrics.annotation.Timed;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.Validate;

import javax.validation.constraints.Null;
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
        } catch (IllegalArgumentException throwable) {
            throw new WebApplicationException(throwable.getMessage(), 400);
        } catch (NullPointerException throwable) {
            throw new WebApplicationException(throwable.getMessage(), 400);
        } catch (Throwable throwable) {
            throw new WebApplicationException(throwable.getMessage(), 500);
        }
    }

    @VisibleForTesting
    Double computeExponentialPdf(DistributionInput input) {
        Validate.notNull(input, "input cannot be null");
        return computeExponentialPdf(input.getLambda(), input.getX());
    }

    @VisibleForTesting
    Double computeExponentialPdf(Double lambda, Double x) {
        Validate.notNull(lambda, "lambda must be specified");
        Validate.isTrue(lambda > 0, "lambda should be greater than 0");
        Validate.notNull(x, "x must be specified");
        Validate.isTrue(x >= 0, "x should be non-negative");
        Double pdf = lambda * exp(-lambda * x);
        if (pdf.equals(0.0)) { // sometimes we get spurious zeros, so try another computation
            pdf = exp(log(lambda) - lambda * x);
        }
        return pdf;
    }
}