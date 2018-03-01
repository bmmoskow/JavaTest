package ben.math.distribution;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ben.math.distribution.resources.DistributionResource;
import ben.math.distribution.health.TemplateHealthCheck;

public class DistributionApplication extends Application<DistributionConfiguration> {
    public static void main(String[] args) throws Exception {
        new DistributionApplication().run(args);
    }

    @Override
    public String getName() {
        return "distribution";
    }

    @Override
    public void initialize(Bootstrap<DistributionConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(DistributionConfiguration configuration,
                    Environment environment) {
        final DistributionResource resource = new DistributionResource(
        );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }
}