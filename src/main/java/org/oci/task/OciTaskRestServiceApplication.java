package org.oci.task;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.oci.task.core.OciTask;
import org.oci.task.db.OciTaskDao;
import org.oci.task.resources.OciTaskResource;

/**
 * @brief Entry point for OCI Task REST Service.
 * @author rajeshkurup@live.com
 */
public class OciTaskRestServiceApplication extends Application<OciTaskRestServiceConfiguration> {

    public static void main(final String[] args) throws Exception {
        new OciTaskRestServiceApplication().run(args);
    }

    private final HibernateBundle<OciTaskRestServiceConfiguration> hibernateBundle =
        new HibernateBundle<OciTaskRestServiceConfiguration>(OciTask.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(OciTaskRestServiceConfiguration configuration) {
                return configuration.getDatabase();
            }
        };

    @Override
    public String getName() {
        return "ocitaskrestservice";
    }

    @Override
    public void initialize(final Bootstrap<OciTaskRestServiceConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(hibernateBundle);

        bootstrap.addBundle(new MigrationsBundle<OciTaskRestServiceConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(OciTaskRestServiceConfiguration configuration) {
                return configuration.getDatabase();
            }
        });

        bootstrap.addBundle(new SwaggerBundle<OciTaskRestServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(OciTaskRestServiceConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(final OciTaskRestServiceConfiguration configuration,
                    final Environment environment) {
        final OciTaskDao ociTaskDao = new OciTaskDao(hibernateBundle.getSessionFactory());

        environment.jersey().register(new OciTaskResource(ociTaskDao));
    }

}
