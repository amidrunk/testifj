package org.testifj.delegate;

import org.junit.Test;
import org.testifj.Predicate;
import org.testifj.ServiceContext;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

@SuppressWarnings("unchecked")
public class ExpectationDelegateConfigurationTest {

    private final ExpectationDelegateConfiguration.Builder emptyBuilder = new ExpectationDelegateConfiguration.Builder();
    private final Predicate predicate = mock(Predicate.class);
    private final Expectation exampleExpectation = mock(Expectation.class);
    private final ExpectationDelegateExtension exampleExtension = mock(ExpectationDelegateExtension.class);
    private final ServiceContext serviceContext = mock(ServiceContext.class);

    @Test
    public void builderOnShouldNotAcceptNullPredicate() {
        expect(() -> emptyBuilder.on(null)).toThrow(AssertionError.class);
    }

    @Test
    public void builderOnThenShouldNotAcceptNullExtension() {
        expect(() -> emptyBuilder.on(predicate).then(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldReturnNullIfNoMatchingExtensionExists() {
        expect(emptyBuilder.build().getExtension(new ExpectationVerificationContextImpl<>(
                exampleExpectation,
                Collections.<ExpectationVerificationContext>emptyList(),
                serviceContext))).toBe(equalTo(null));
    }

    @Test
    public void getExtensionShouldNotAcceptNullExpectation() {
        expect(() -> emptyBuilder.build().getExtension(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getExtensionShouldReturnMatchingExtension() {
        final ExpectationDelegateConfiguration configuration = emptyBuilder
                .on(predicate).then(exampleExtension)
                .build();

        when(predicate.test(any())).thenReturn(true);

        expect(configuration.getExtension(new ExpectationVerificationContextImpl<>(
                exampleExpectation,
                Collections.emptyList(),
                serviceContext))).toBe(exampleExtension);
    }

}
