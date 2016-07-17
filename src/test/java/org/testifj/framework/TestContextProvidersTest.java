package org.testifj.framework;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.RecordingTestContextProvider;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

    @Test
    public void itShouldBePossibleToOverrideAndRestoreTestContextProvider() {
        final TestContextProvider newProvider = mock(TestContextProvider.class);
        final TestContextProvider originalProvider = TestContextProviders.configuredTestContextProvider();
        final TestContextProviders.TestContextProviderOverride override = TestContextProviders.overrideTestContextProvider(newProvider);

        assertSame(newProvider, TestContextProviders.configuredTestContextProvider());

        override.restore();

        assertSame(originalProvider, TestContextProviders.configuredTestContextProvider());
    }
}