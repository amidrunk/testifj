package org.testifj.delegate;

import org.junit.Before;
import org.junit.Test;
import io.recode.Caller;
import org.testifj.Predicate;
import org.testifj.ServiceContext;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

@SuppressWarnings("unchecked")
public class DefaultExpectationDelegateTest {

    private final Predicate examplePredicate = mock(Predicate.class);

    private final ExpectationDelegateExtension exampleExtension = mock(ExpectationDelegateExtension.class);

    private final ExpectationDelegateConfiguration exampleConfiguration = new ExpectationDelegateConfiguration.Builder()
            .on(examplePredicate).then(exampleExtension)
            .build();

    private final ServiceContext serviceContext = mock(ServiceContext.class);
    private final DefaultExpectationDelegate exampleDelegate = new DefaultExpectationDelegate(serviceContext, exampleConfiguration);
    private final Caller exampleCaller = Caller.me();

    @Before
    public void setup() {
        when(examplePredicate.test(any())).thenReturn(true);
    }

    @Test
    public void constructorShouldNotAcceptNullServiceContextOrConfiguration() {
        expect(() -> new DefaultExpectationDelegate(null, exampleConfiguration)).toThrow(AssertionError.class);
        expect(() -> new DefaultExpectationDelegate(serviceContext, null)).toThrow(AssertionError.class);
    }

    @Test
    public void startExpectationShouldReturnNonNullExpectation() {
        given(exampleDelegate.startExpectation()).then(it -> {
            expect(it).not().toBe(equalTo(null));
        });
    }

    @Test
    public void singleExpectationCanBeHandled() {
        final OnGoingExpectation onGoingExpectation = exampleDelegate.startExpectation();
        final Expectation expectation = mock(Expectation.class);
        final ExpectationVerification expectedVerification = mock(ExpectationVerification.class);

        when(exampleExtension.verify(any())).thenReturn(expectedVerification);

        final ExpectationVerification actualVerification = onGoingExpectation.complete(expectation);

        expect(actualVerification).toBe(expectedVerification);
    }

    @Test
    public void expectationShouldFailWithExceptionIfConfigurationIsIncomplete() {
        when(examplePredicate.test(any())).thenReturn(false);

        final OnGoingExpectation onGoingExpectation = exampleDelegate.startExpectation();

        expect(() -> onGoingExpectation.complete(mock(Expectation.class))).toThrow(UnsupportedOperationException.class);
    }

}
