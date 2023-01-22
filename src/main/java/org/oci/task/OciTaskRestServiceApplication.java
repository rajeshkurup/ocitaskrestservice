package org.oci.task;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.oci.task.core.OciTask;
import org.oci.task.db.OciTaskDao;
import org.oci.task.resources.OciTaskResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

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
    }

    @Override
    public void run(final OciTaskRestServiceConfiguration configuration,
                    final Environment environment) {
        final OciTaskDao ociTaskDao = new OciTaskDao(hibernateBundle.getSessionFactory());

        environment.jersey().register(new OciTaskResource(ociTaskDao));

        final FilterRegistration.Dynamic corsFilter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Cache-Control,If-Modified-Since,Pragma,Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");
        corsFilter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

}
