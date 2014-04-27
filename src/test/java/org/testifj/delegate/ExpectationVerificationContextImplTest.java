package org.testifj.delegate;

import org.junit.Test;
import org.testifj.ServiceContext;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

@SuppressWarnings("unchecked")
public class ExpectationVerificationContextImplTest {

    private final Expectation exampleExpectation1 = mock(Expectation.class);

    private final Expectation exampleExpectation2 = mock(Expectation.class);

    private final ServiceContext serviceContext = mock(ServiceContext.class);

    private final ExpectationVerificationContext exampleLeaf = new ExpectationVerificationContextImpl(
            exampleExpectation2,
            Collections.<ExpectationVerificationContext>emptyList(),
            serviceContext);

    private final ExpectationVerificationContextImpl exampleRootNode = new ExpectationVerificationContextImpl(
            exampleExpectation1,
            Arrays.asList(exampleLeaf),
            serviceContext);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new ExpectationVerificationContextImpl(null, Collections.emptyList(), serviceContext)).toThrow(AssertionError.class);
        expect(() -> new ExpectationVerificationContextImpl(mock(Expectation.class), null, serviceContext)).toThrow(AssertionError.class);
        expect(() -> new ExpectationVerificationContextImpl(mock(Expectation.class), Collections.emptyList(), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        expect(exampleRootNode.getExpectation()).toBe(exampleExpectation1);
        expect(exampleRootNode.getSubContexts()).toBe(Arrays.asList(exampleLeaf));
    }

    @Test
    public void getDependencyShouldResolveComponentThroughServiceContext() {
        when(serviceContext.get(eq(String.class))).thenReturn("foo");

        expect(exampleRootNode.get(String.class)).toBe("foo");

        verify(serviceContext).get(eq(String.class));
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleRootNode).toBe(equalTo(exampleRootNode));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleRootNode).not().toBe(equalTo(null));
        expect((Object) exampleRootNode).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualPropertyValuesShouldBeEqual() {
        final ExpectationVerificationContextImpl other = new ExpectationVerificationContextImpl(
                exampleExpectation1,
                Arrays.asList(exampleLeaf),
                serviceContext);

        expect(exampleRootNode).toBe(equalTo(other));
        expect(exampleRootNode.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        given(exampleRootNode.toString()).then(it -> {
            expect(it).to(containString(exampleExpectation1.toString()));
            expect(it).to(containString(exampleExpectation2.toString()));
        });
    }
}
