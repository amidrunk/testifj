package org.testifj;

import org.testifj.annotations.DSL;
import org.testifj.impl.ExpectedExceptionNotThrownImpl;
import org.testifj.lang.Procedure;
import org.testifj.matchers.core.Equal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@DSL
@SuppressWarnings("unchecked")
public final class Expect {

    /**
     * <p>
     * Initializes an expectation given an instance. This is the entry-point towards the DSL for specifying
     * a requirement on an instance. Examples:
     * </p>
     *
     * <p>
     * <dir>
     *     <li><code>expect("foo").toBe("bar"); // failure</code></li>
     *     <li><code>expect(Collections.emptyList()).toBe(collectionThat(isEmpty()));</code></li>
     *     <li><code>expect(1).toBe(lessThan(2));</code></li>
     *     <li><code>expect(2).toBe(lessThan(2).or(equalTo(2)));</code></li>
     * </dir>
     * </p>
     *
     * <p>
     * Whenever an <code>expect(...).toBe(...)</code> fails, the configured expectation failure handler
     * will be called. By default, any such failure will cause the tests to by aborted by throwing an
     * <code>AssertionError</code> with a descriptive message.
     * </p>
     *
     * @param instance The instance that subsequent matchers are being applied to.
     * @param <T> The type of the instance. This restricts the permissible matchers that can be used in the DSL.
     * @return A DLS continuation that allows further specification of the instance constraint.
     */
    public static <T> ExpectValueContinuation<T> expect(T instance) {
        final List<StackTraceElement> stackTrace = Arrays.asList(Thread.currentThread().getStackTrace());
        final Caller caller = new Caller(stackTrace.subList(1, stackTrace.size()), 1);

        return new ExpectValueContinuation<T>() {
            @Override
            public void to(Matcher<T> matcher) {
                if (!matcher.matches(instance)) {
                    final ValueMismatchFailureImpl failure = new ValueMismatchFailureImpl(caller, matcher, Optional.empty(), instance);

                    Configuration.get().getExpectationFailureHandler().handleExpectationFailure(failure);
                }
            }

            @Override
            public void toBe(T expectedValue) {
                final Matcher<T> matcher = Equal.equal(expectedValue);

                if (!matcher.matches(instance)) {
                    final ValueMismatchFailureImpl failure = new ValueMismatchFailureImpl(caller, matcher, Optional.of(expectedValue), instance);

                    Configuration.get().getExpectationFailureHandler().handleExpectationFailure(failure);
                }
            }
        };
    }

    /**
     * Initializes an expectation on a void-return procedure. Expectations on the outcome of the
     * procedure can be tested, which is exceptional or non-exceptional.
     *
     * @param procedure The procedure executed by the expectation.
     * @return A continuance that allows for further expectation specification.
     */
    public static ExpectProcedureContinuation expect(Procedure procedure) {
        final List<StackTraceElement> stackTrace = Arrays.asList(Thread.currentThread().getStackTrace());
        final Caller caller = new Caller(stackTrace.subList(1, stackTrace.size()), 1);

        return expectation -> {
            Outcome outcome;

            try {
                procedure.call();
                outcome = Outcome.successful(caller);
            } catch (Throwable e) {
                outcome = Outcome.exceptional(caller, e);
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
                if (!outcome.isExceptional()) {
                    Configuration.get().getExpectationFailureHandler().handleExpectationFailure(
                            new ExpectedExceptionNotThrownImpl(outcome.getCaller(), exceptionType));
                }

                assert outcome.isExceptional() : "Exception of type '" + exceptionType.getName() + "' was expected";

                if (!exceptionType.isInstance(outcome.getException().get())) {
                    if (outcome.getException().get() instanceof RuntimeException) {
                        throw (RuntimeException) outcome.getException().get();
                    } else {
                        throw new AssertionError("Expectation failed with exception", outcome.getException().get());
                    }
                }

                // assert exceptionType.isInstance(outcome.getException()) : "Exception of type '" + exceptionType.getName() + "' expected, was " + outcome.getException();
            };

            final Capture<Outcome> capturedOutcome = new Capture<>();

            to(expectation.capture(capturedOutcome));

            return matcher -> {
                assert ((Matcher) matcher).matches(capturedOutcome.get().getException().get());
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

    /**
     * Runtime configuration class of the Expect class. The default configuration will handle expectation
     * failures by aborting the execution and logging a descriptive message. This can be overridden.
     */
    public static final class Configuration {

        /**
         * Reference to the currently active configuration. Initialized to a default configuration that
         * handles expectation failures by aborting the test execution and reporting the error to the
         * user.
         */
        private static final AtomicReference<Configuration> CONFIGURATION_REFERENCE = new AtomicReference<>(
                Configuration.newBuilder()
                        .configureExpectationFailureHandler(new DefaultExpectationFailureHandler.Builder().build())
                        .build());

        private final ExpectationFailureHandler expectationFailureHandler;

        private Configuration(ExpectationFailureHandler expectationFailureHandler) {
            this.expectationFailureHandler = expectationFailureHandler;
        }

        public ExpectationFailureHandler getExpectationFailureHandler() {
            return expectationFailureHandler;
        }

        /**
         * Returns the currently active configuration. This is never null.
         *
         * @return The currently active configuration.
         */
        public static Configuration get() {
            return CONFIGURATION_REFERENCE.get();
        }

        /**
         * Changes the active configuration to the provided configuration. Subsequent expectation failures etc
         * will be handled by the configured expectation failure handler.
         *
         * @param configuration The configuration that should become active. This can't be null.
         * @return The old configuration.
         */
        public static Configuration configure(Configuration configuration) {
            assert configuration != null : "Configuration can't be null";

            return CONFIGURATION_REFERENCE.getAndSet(configuration);
        }

        /**
         * Creates a new builder that provides support for creating a new configuration instance.
         *
         * @return A new builder that provides support for creating a configuration.
         */
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

}
