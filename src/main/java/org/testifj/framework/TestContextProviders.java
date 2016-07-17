package org.testifj.framework;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public class TestContextProviders {

    private static final AtomicReference<TestContextProvider> DEFAULT_TEST_CONTEXT_PROVIDER = new AtomicReference<>();

    public static List<TestContextProvider> allFromClasspath() {
        final ServiceLoader<TestContextProvider> serviceLoader = ServiceLoader.load(TestContextProvider.class);
        final LinkedList<TestContextProvider> result = new LinkedList<>();

        serviceLoader.forEach(result::add);

        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the <code>TestContextProvider</code> currently configured. By default, this will fallback
     * to the <code>DefaultTestContextProvider</code> which will implement the default testifj expectation behaviour.
     * It is - however - possible to override this using the Java service loader to change the behaviour, e.g.
     * for test or other non-standard behaviour. If multiple test context providers are configured, the first one
     * will currently be selected.
     *
     * @return The configured <code>TestContextProvider</code>.
     */
    public static TestContextProvider configuredTestContextProvider() {
        final TestContextProvider testContextProvider = DEFAULT_TEST_CONTEXT_PROVIDER.get();

        if (testContextProvider == null) {
            final List<TestContextProvider> candidates = allFromClasspath();

            if (candidates.isEmpty()) {
                final DefaultTestContextProvider defaultProvider = new DefaultTestContextProvider();

                if (DEFAULT_TEST_CONTEXT_PROVIDER.compareAndSet(null, defaultProvider)) {
                    return defaultProvider;
                }
            } else {
                final TestContextProvider candidate = candidates.get(0);

                if (DEFAULT_TEST_CONTEXT_PROVIDER.compareAndSet(null, candidate)) {
                    return candidate;
                }
            }
        }

        return DEFAULT_TEST_CONTEXT_PROVIDER.get();
    }

    public static TestContextProviderOverride overrideTestContextProvider(TestContextProvider newProvider) {
        final TestContextProvider originalProvider = DEFAULT_TEST_CONTEXT_PROVIDER.getAndSet(newProvider);
        return () -> DEFAULT_TEST_CONTEXT_PROVIDER.set(originalProvider);
    }

    public interface TestContextProviderOverride {

        void restore();
    }
}
