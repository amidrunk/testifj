package org.testifj;

import org.testifj.annotations.DSL;
import org.testifj.lang.Procedure;
import org.testifj.matchers.core.Equal;

import java.util.concurrent.atomic.AtomicReference;

@DSL
@SuppressWarnings("unchecked")
public final class Expect {

    public static final class Configuration {

        private static final AtomicReference<Configuration> CONFIGURATION_REFERENCE = new AtomicReference<>(
                Configuration.newBuilder()
                        .configureExpectationFailureHandler(new DefaultExpectationFailureHandler())
                        .build());

        private final ExpectationFailureHandler expectationFailureHandler;

        private Configuration(ExpectationFailureHandler expectationFailureHandler) {
            this.expectationFailureHandler = expectationFailureHandler;
        }

        public static Configuration get() {
            return CONFIGURATION_REFERENCE.get();
        }

        public static Configuration configure(Configuration configuration) {
            assert configuration != null : "Configuration can't be null";

            return CONFIGURATION_REFERENCE.getAndSet(configuration);
        }

        public static NewBuilderContinuation newBuilder() {
            return handler -> () -> new Configuration(handler);
        }

        @FunctionalInterface
        public interface NewBuilderContinuation {

            ExpectationFailureHandlerContinuation configureExpectationFailureHandler(ExpectationFailureHandler handler);

        }

        @FunctionalInterface
        public interface ExpectationFailureHandlerContinuation {

            Configuration build();

        }

    }

    public static <T> ExpectValueContinuation<T> expect(T instance) {
        return new ExpectValueContinuation<T>() {
            @Override
            public void to(Matcher<T> matcher) {
                assert matcher.matches(instance) : "Instance does not match. Was: \"" + instance + "\"";
            }
        };
    }

    public interface ExpectValueContinuation<T> {

        void to(Matcher<T> matcher);

        default void toBe(Matcher<T> matcher) {
            to(matcher);
        }

        default void toBe(T instance) {
            toBe(Equal.equal(instance));
        }

        default ExpectValueContinuation<T> not() {
            return matcher -> ExpectValueContinuation.this.to(instance -> !matcher.matches(instance));
        }

    }

    /**
     * Initializes an expectation on a void-return procedure. Expectations on the outcome of the
     * procedure can be tested, which is exceptional or non-exceptional.
     *
     * @param procedure The procedure executed by the expectation.
     * @return A continuance that allows for further expectation specification.
     */
    public static ExpectProcedureContinuation expect(Procedure procedure) {
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
    public interface ExpectProcedureContinuation {

        void to(Expectation<Outcome> expectation);

        default ExpectProcedureContinuation not() {
            return expectation -> ExpectProcedureContinuation.this.to(outcome -> {
                boolean failed = false;

                try {
                    expectation.verify(outcome);
                } catch (AssertionError e) {
                    failed = true;
                }

                if (!failed) {
                    // TODO message
                    throw new AssertionError("Inverted");
                }
            });
        }

        default ToThrowContinuance<Throwable> toThrow() {
            return toThrow(Throwable.class);
        }

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
