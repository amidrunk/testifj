package org.testifj;

import org.testifj.delegate.*;
import org.testifj.lang.decompile.CallerDecompiler;
import org.testifj.lang.classfile.ClassFileReader;
import org.testifj.lang.decompile.Decompiler;
import org.testifj.lang.decompile.impl.CallerDecompilerImpl;
import org.testifj.lang.classfile.impl.ClassFileReaderImpl;
import org.testifj.lang.decompile.impl.CodePointerCodeGenerator;
import org.testifj.lang.decompile.impl.DecompilerImpl;

import java.util.concurrent.atomic.AtomicReference;

public final class Configuration {

    private static final AtomicReference<Configuration> CONFIGURATION_REFERENCE = new AtomicReference<>(getDefaultConfiguration());

    private final ServiceContext serviceContext;

    private Configuration(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }


    public Configuration withServiceContext(ServiceContext serviceContext) {
        assert serviceContext != null : "Service context can't be null";

        return new Configuration(serviceContext);
    }

    public static Configuration get() {
        return CONFIGURATION_REFERENCE.get();
    }

    public static Configuration configure(Configuration configuration) {
        assert configuration != null : "Configuration can't be null";

        return CONFIGURATION_REFERENCE.getAndSet(configuration);
    }

    @SuppressWarnings("unchecked")
    private static Configuration getDefaultConfiguration() {
        final ServiceContext componentServiceContext = new ServiceContextFromConfiguration(Configuration::get);

        final ExpectationDelegate expectationDelegate = new DefaultExpectationDelegate(componentServiceContext, new ExpectationDelegateConfiguration.Builder()
                .on(e -> e.getExpectation() instanceof GivenThenExpectation).then((ExpectationDelegateExtension) new GivenThenExpectationDelegateExtension())
                .build());

        final Decompiler decompiler = new DecompilerImpl();
        final ClassFileReader classFileReader = new ClassFileReaderImpl();
        final CallerDecompiler callerDecompiler = new CallerDecompilerImpl(classFileReader, decompiler);
        final CodePointerCodeGenerator codePointerCodeGenerator = new CodePointerCodeGenerator(decompiler);

        final ServiceContext serviceContext = ServiceContextImpl.newBuilder()
                .registerComponent(expectationDelegate)
                .registerComponent(decompiler)
                .registerComponent(classFileReader)
                .registerComponent(callerDecompiler)
                .registerComponent(codePointerCodeGenerator)
                .build();

        return new Configuration(serviceContext);
    }
}
