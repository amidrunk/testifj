package org.testifj;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class ServiceContextBuilderTest {

    @Test
    public void builderShouldNotAcceptRegistrationOfNullComponent() {
        expect(() -> ServiceContextImpl.newBuilder().registerComponent(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getShouldNotAcceptNullComponentType() {
        expect(() -> ServiceContextImpl.newBuilder().build().get(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getShouldFailIfMatchingComponentDoesNotExists() {
        given(ServiceContextImpl.newBuilder().build()).then(it -> {
            expect(() -> it.get(String.class)).toThrow(DependencyResolutionException.class);
        });
    }

    @Test
    public void getShouldReturnMatchingRegisteredComponent() {
        final ServiceContext serviceContext = ServiceContextImpl.newBuilder()
                .registerComponent("foo")
                .build();

        expect(serviceContext.get(String.class)).toBe("foo");
    }

    @Test
    public void getShouldFailIfMultipleMatchingComponentsExist() {
        final ServiceContext serviceContext = ServiceContextImpl.newBuilder()
                .registerComponent("foo")
                .registerComponent("bar")
                .build();

        expect(() -> serviceContext.get(String.class)).toThrow(DependencyResolutionException.class);
    }

}
