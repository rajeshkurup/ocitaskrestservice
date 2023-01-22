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
    }

}
