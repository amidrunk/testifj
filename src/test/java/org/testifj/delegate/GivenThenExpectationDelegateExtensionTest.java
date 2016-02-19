package org.testifj.delegate;

import org.junit.Test;
import org.testifj.Action;
import org.testifj.Caller;
import org.testifj.ServiceContext;
import org.testifj.ServiceContextImpl;
import org.testifj.lang.decompile.impl.CodeLocationDecompilerImpl;
import org.testifj.lang.codegeneration.impl.CodePointerCodeGenerator;

import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.StringShould.containString;

public class GivenThenExpectationDelegateExtensionTest {

    private final GivenThenExpectationDelegateExtension extension = new GivenThenExpectationDelegateExtension();

    private final Action exampleAction = mock(Action.class);

    private final GivenThenExpectation exampleCompliantExpectation = getExampleCompliantExpectation(exampleAction);

    private final ServiceContext serviceContext = ServiceContextImpl.newBuilder()
            .registerComponent(new CodePointerCodeGenerator())
            .registerComponent(new CodeLocationDecompilerImpl())
            .build();

    @Test
    public void verifyShouldNotAcceptNullExpectation() {
        expect(() -> extension.verify(null)).toThrow(AssertionError.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verifyShouldCallActionAndReturnCompliantExpectationIfActionSucceeds() throws Exception {
        final ExpectationVerificationContextImpl<GivenThenExpectation> expectation = new ExpectationVerificationContextImpl<>(
                exampleCompliantExpectation,
                Collections.emptyList(),
                serviceContext);

        final ExpectationVerification verification = extension.verify(expectation);

        verify(exampleAction, times(1)).execute(eq("foo"));

        expect(verification.isCompliant()).toBe(true);
        expect(verification.getExpectation()).toBe((ExpectationVerificationContext) expectation);
        expect(verification.getExpectationDescription().toString()).to(containString("\"foo\""));
    }

    private GivenThenExpectation getExampleCompliantExpectation(Action action) {
        given("foo").then(str -> {
            expect(str).toBe("foo");
            expect(str.length()).toBe(3);
        });

        return new GivenThenExpectation(Caller.adjacent(-5), "foo", action);
    }

}
