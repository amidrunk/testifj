package org.testifj;

import org.junit.After;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.delegate.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.delegate.ExpectationMatchers.isGivenThenWith;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class GivenTest {

    private final Configuration originalConfiguration = Configuration.get();
    private final ExpectationDelegate expectationDelegate = mock(ExpectationDelegate.class);

    @After
    public void restoreConfiguration() {
        Configuration.configure(originalConfiguration);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenShouldCreateNewOnGoingExpectationAndCompleteOnThen() {
        Configuration.configure(originalConfiguration.withServiceContext(getServiceContext()));

        final OnGoingExpectation onGoingExpectation = mock(OnGoingExpectation.class);
        final Action action = mock(Action.class);
        final ExpectationVerification compliantVerification = ExpectationVerificationImpl.compliant(mock(ExpectationVerificationContext.class), mock(Description.class));

        when(expectationDelegate.startExpectation()).thenReturn(onGoingExpectation);
        when(onGoingExpectation.complete(any())).thenReturn(compliantVerification);

        given("foo").then(action);

        final InOrder inOrder = Mockito.inOrder(expectationDelegate, onGoingExpectation);

        inOrder.verify(expectationDelegate).startExpectation();
        inOrder.verify(onGoingExpectation).complete(argThat(isGivenThenWith(getClass(), "givenShouldCreateNewOnGoingExpectationAndCompleteOnThen", "foo", action)));

        verifyNoMoreInteractions(expectationDelegate, onGoingExpectation);
        verifyZeroInteractions(action);
    }

    @Test
    public void specificationShouldBeCompletedIfFulfilled() {
        try {
            given(new StringBuilder()).
                    when((s) -> s.append("hello")).
                    then((s) -> s.toString().equals("hello"));
        } catch (Throwable e) {
            fail("Successful specification should not cause exception: " + e);
        }
    }

    @Test
    public void specificationShouldFailOnImplementationMismatch() {
        boolean failed = false;

        try {
            given(new StringBuilder()).
                    when((s) -> s.append("foo")).
                    then((s) -> expect(s.toString()).toBe("bar"));
        } catch (AssertionError e) {
            failed = true;
            e.printStackTrace();
        }

        assertTrue("Specification should fail for implementation mismatch", failed);
    }

    @Test
    public void givenThenShouldFailIfVerificationFails() {
        expect(() -> {
            given("str").then(str -> {
                expect(str.length()).toBe(4);
            });
        }).toThrow(AssertionError.class).withMessage(equalTo("Expected str.length() => 3 to be 4"));
    }

    @Test
    public void givenThenShouldReturnIfNoExpectationFails() {
        given("str").then(str -> {
            expect(str.length()).toBe(3);
        });
    }

    @Test
    public void givenWhenThenShouldExecuteInOrder() throws Exception {
        final Action whenAction = mock(Action.class);
        final Procedure thenProcedure = mock(Procedure.class);

        given("str").when(whenAction).then(thenProcedure);

        final InOrder inOrder = Mockito.inOrder(whenAction, thenProcedure);

        inOrder.verify(whenAction).execute(eq("str"));
        inOrder.verify(thenProcedure).call();
    }

    private ServiceContext getServiceContext() {
        final ServiceContext serviceContext = mock(ServiceContext.class);
        when(serviceContext.get(eq(ExpectationDelegate.class))).thenReturn(expectationDelegate);
        return serviceContext;
    }

}
