package org.testifj;

import org.junit.After;
import org.junit.Test;
import org.testifj.delegate.ExpectationDelegate;
import org.testifj.delegate.ExpectationVerification;
import org.testifj.delegate.GivenThenExpectation;
import org.testifj.delegate.OnGoingExpectation;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ConfigurationTest {

    private final Configuration originalConfiguration = Configuration.get();

    @After
    public void restoreConfiguration() {
        Configuration.configure(originalConfiguration);
    }

    @Test
    public void defaultConfigurationShouldNotBeNull() {
        expect(Configuration.get()).not().toBe(equalTo(null));
    }

    @Test
    public void defaultConfigurationShouldContainServiceContext() {
        expect(Configuration.get().getServiceContext()).not().toBe(equalTo(null));
    }

    @Test
    public void configurationCanBeChanged() {
        final Configuration newConfiguration = Configuration.get().withServiceContext(mock(ServiceContext.class));
        final Configuration oldConfiguration = Configuration.configure(newConfiguration);

        expect(oldConfiguration).toBe(originalConfiguration);
        expect(Configuration.get()).toBe(newConfiguration);
    }

    @Test
    public void configureShouldNotAcceptNullConfiguration() {
        expect(() -> Configuration.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void withExpectationDelegateShouldNotAcceptNullServiceContext() {
        expect(() -> Configuration.get().withServiceContext(null)).toThrow(AssertionError.class);
    }

    @Test
    public void defaultConfigurationShouldSupportGivenThen() {
        final OnGoingExpectation onGoingExpectation = Configuration.get().getServiceContext().get(ExpectationDelegate.class).startExpectation();
        final Caller caller = Caller.adjacent(4);
        final ExpectationVerification verification = onGoingExpectation.complete(new GivenThenExpectation(caller, "foo", mock(Action.class)));

        expect(verification).not().toBe(equalTo(null));
        expect(verification.isCompliant()).toBe(true);
    }

}
