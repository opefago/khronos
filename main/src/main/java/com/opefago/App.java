package com.opefago;

import com.opefago.configuration.AppConfiguration;
import com.opefago.configuration.ConfigModule;
import com.opefago.lib.idempotence.config.IdempotentConfigModule;
import com.opefago.lib.kafka.config.KafkaConfigModule;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class App extends Application<AppConfiguration> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }
    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {

    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(
                GuiceBundle
                        .builder()
                        .enableAutoConfig("com.opefago")
                        .modules(
                                new ConfigModule(),
                                new IdempotentConfigModule(),
                                new KafkaConfigModule("com.opefago")
                        )
                        .printWebMappings()
                        .build()
        );
    }
}
