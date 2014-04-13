package org.testifj;

import org.testifj.lang.Procedure;
import org.testifj.matchers.core.Equal;

@SuppressWarnings("unchecked")
public final class Expect {

    public static <T> ExpectInstanceContinuance<T> expect(T instance) {
        return new ExpectInstanceContinuance<T>() {
            @Override
            public void to(Matcher<T> matcher) {
                assert matcher.matches(instance) : "Instance does not match. Was: \"" + instance + "\"";
            }

            @Override
            public void toBe(Matcher<T> matcher) {
                assert matcher.matches(instance) : "Instance does not match. Was: \"" + instance + "\"";
            }
        };
    }

    public interface ExpectInstanceContinuance<T> {

        void to(Matcher<T> matcher);

        void toBe(Matcher<T> matcher);

        default void toBe(T instance) {
            toBe(Equal.equal(instance));
        }

    }

    /**
     * Initializes an expectation on a void-return procedure. Expectations on the outcome of the
     * procedure can be tested, which is exceptional or non-exceptional.
     *
     * @param procedure The procedure executed by the expectation.
     * @return A continuance that allows for further expectation specification.
     */
    public static ExpectProcedureContinuance expect(Procedure procedure) {
        return expectation -> {
            Outcome outcome;

            try {
                procedure.call();
                outcome = Outcome.successful();
            } catch (Throwable e) {
                outcome = Outcome.exceptional(e);
            }

            expectation.verify(outcome);
        };
    }

    @FunctionalInterface
    public interface ExpectProcedureContinuance {

        void to(Expectation<Outcome> expectation);

        default <E extends Throwable> ToThrowContinuance<E> toThrow(Class<E> exceptionType) {
            final Expectation<Outcome> expectation = outcome -> {
                assert outcome.isExceptional() : "Exception of type '" + exceptionType.getName() + "' was expected";
                assert exceptionType.isInstance(outcome.getException()) : "Exception of type '" + exceptionType.getName() + "' expected, was " + outcome.getException();
            };

            final Capture<Outcome> capturedOutcome = new Capture<>();

            to(expectation.capture(capturedOutcome));

            return matcher -> {
                assert ((Matcher) matcher).matches(capturedOutcome.get().getException());
            };
        }

    }

    @FunctionalInterface
    public interface ToThrowContinuance<E extends Throwable> {

        void where(Matcher<E> matcher);

        default void withMessage(Matcher<String> matcher) {
            where((e) -> matcher.matches(e.getMessage()));
        }
    }

}
