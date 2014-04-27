package org.testifj;

import org.testifj.delegate.ExpectationDelegate;
import org.testifj.delegate.ExpectationVerification;
import org.testifj.delegate.GivenThenExpectation;
import org.testifj.delegate.OnGoingExpectation;

import java.util.Arrays;

/**
 * TODO should delegate everything:
 *
 * Since given-then and given-when-then can nest expectations, we should retain the resulting structure
 * in the expectation handling. For instance:
 * given("foo").then(str -> expect(str).toBe("foo"));
 *
 * Should result in GivenThen(children = ExpectTo)
 *
 * The structure should be retained, THEN we should traverse everything (when the root is finished) and complete.
 *
 * Should be:
 *
 * final OngoingExpectation ongoingExpectation = Configuration.get().getExpectationHandler().createOngoingExpectation();
 *
 * ongoingExpectation.complete(new GivenThenExpectation(...));
 *
 * That establishes the relationship between the different expectations...
 *
 * This formulates a specification? Or a contract? Blueprint?
 *
 * final Specification specification = ongoingExpectation.complete(...);
 *
 * NO
 *
 * final Contract contract = ongoingExpectation.complete(...);
 *
 * for (Contract.Clause clause : contract) {
 *     co
 * }
 *
 */
@SuppressWarnings("unchecked")
public final class Given {

    public static <T> GivenContinuance<T> given(final T instance) {
        final Caller caller = new Caller(Arrays.asList(Thread.currentThread().getStackTrace()), 2);
        final OnGoingExpectation onGoingExpectation = Configuration.get().getServiceContext().get(ExpectationDelegate.class).startExpectation();

        return new GivenContinuance<T>() {
            @Override
            public void then(Action<T> action) {
                final ExpectationVerification verification = onGoingExpectation.complete(new GivenThenExpectation(caller, instance, (Action) action));

                if (!verification.isCompliant()) {
                    throw new AssertionError(verification.getVerificationFailureDescription().get().toString());
                }
            }

            @Override
            public WhenContinuance<T> when(Action<T> whenAction) {
                return new WhenContinuance<T>() {
                    @Override
                    public void then(Action<T> thenAction) {
                        try {
                            whenAction.execute(instance);
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }

                        try {
                            thenAction.execute(instance);
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }
                    }

                    @Override
                    public void then(Procedure procedure) {
                        try {
                            whenAction.execute(instance);
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }

                        try {
                            procedure.call();
                        } catch (RuntimeException|AssertionError e) {
                            throw e;
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }
                    }
                };
            }
        };
    }

    public static interface GivenContinuance<T> {

        WhenContinuance<T> when(Action<T> action);

        void then(Action<T> action);

    }

    public static interface WhenContinuance<T> {

        void then(Action<T> action);

        void then(Procedure procedure);

    }

}
