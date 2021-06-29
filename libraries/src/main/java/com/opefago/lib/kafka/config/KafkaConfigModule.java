package com.opefago.lib.kafka.config;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.opefago.lib.ReflectionUtils;
import com.opefago.lib.kafka.KafkaListener;

public class KafkaConfigModule extends AbstractModule {
    private final String packageName;
    public KafkaConfigModule(final String packageName){
        this.packageName = packageName;
    }

    @Override
    protected void configure() {
        Multibinder<Object> multiBinder = Multibinder.newSetBinder(binder(),Object.class);
        ReflectionUtils.getClassesAnnotatedWith(
                packageName,
                KafkaListener.class
        ).forEach(c->multiBinder.addBinding().to(c).asEagerSingleton());
    }
}
