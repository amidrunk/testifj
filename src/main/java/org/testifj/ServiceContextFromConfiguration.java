package org.testifj;

import java.util.function.Supplier;

public final class ServiceContextFromConfiguration implements ServiceContext {

    private final Supplier<Configuration> configurationSupplier;

    public ServiceContextFromConfiguration(Supplier<Configuration> configurationSupplier) {
        assert configurationSupplier != null : "Configuration supplier can't be null";

        this.configurationSupplier = configurationSupplier;
    }

    @Override
    public <T> T get(Class<T> type) {
        return configurationSupplier.get().getServiceContext().get(type);
    }
}
