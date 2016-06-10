package org.testifj.framework;

import org.junit.Test;
import org.testifj.RecordingTestContextProvider;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestContextProvidersTest {

    @Test
    public void testContextProvidersCanBeResolvedFromClasspath() {
        final List<TestContextProvider> providers = TestContextProviders.allFromClasspath();

        assertEquals(1, providers.size());
        assertTrue(providers.get(0) instanceof RecordingTestContextProvider);
    }

    @Test
    public void configuredTestContextProviderCanBeRetrieved() {
        final TestContextProvider provider = TestContextProviders.configuredTestContextProvider();

        assertTrue(provider instanceof RecordingTestContextProvider);
    }
}