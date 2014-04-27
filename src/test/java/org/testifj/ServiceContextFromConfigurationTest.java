package org.testifj;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class ServiceContextFromConfigurationTest {

    @Test
    public void constructorShouldNotAcceptNullConfigurationSupplier() {
        expect(() -> new ServiceContextFromConfiguration(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getShouldLookupComponentThroughSuppliedConfiguration() {
        final ServiceContext configuredServiceContext = mock(ServiceContext.class);
        final Configuration configuration = Configuration.get()
                .withServiceContext(configuredServiceContext);

        given(new ServiceContextFromConfiguration(() -> configuration)).then(it -> {
            when(configuredServiceContext.get(eq(String.class))).thenReturn("foo");

            expect(it.get(String.class)).toBe("foo");

            verify(configuredServiceContext, times(1)).get(eq(String.class));
        });
    }

}
