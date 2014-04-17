package org.testifj;

import org.hamcrest.*;
import org.hamcrest.Description;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ExpectTest {

    private final ExpectationFailureHandler expectationFailureHandler = mock(ExpectationFailureHandler.class);

    @Test
    public void expectToThrowShouldSucceedIfConditionsAreFulfilled() {
        expect(() -> {
            throw new IllegalArgumentException("foo");
        }).toThrow(IllegalArgumentException.class).where((e) -> e.getMessage().equals("foo"));
    }

    @Test
    public void expectToThrowShouldFailIfWhereSpecificationIsNotFulfilled() {
        boolean failed = false;

        try {
            expect(() -> {
                throw new IllegalArgumentException("foo");
            }).toThrow(IllegalArgumentException.class).where((e) -> e.getMessage().equals("bar"));
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue("Unfulfilled specification should fail", failed);
    }

    @Test
    public void expectToThrowShouldSucceedIfExceptionWithoutFurtherSpecificationIsThrown() {
        try {
            expect(() -> {
                throw new IllegalArgumentException();
            }).toThrow(IllegalArgumentException.class);
        } catch (AssertionError e) {
            fail();
        }
    }

    @Test
    public void expectToThrowShouldFailIfNoExceptionIsThrown() {
        boolean failed = false;

        try {
            expect(() -> {}).toThrow(IllegalArgumentException.class);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue("Specification should fail if no exception is thrown", failed);
    }

    @Test
    public void expectToThrowCanBeCreatedWithMessageMatcher() {
        expect(() -> {
            throw new IllegalArgumentException("foo");
        }).toThrow(IllegalArgumentException.class).withMessage((s) -> s.equals("foo"));
    }

    @Test
    public void expectToThrowWithMessageSpecificationShouldFailIfMessageDoesNotMatch() {
        boolean failed = false;

        try {
            expect(() -> {
                throw new IllegalArgumentException("foo");
            }).toThrow(IllegalArgumentException.class).withMessage((s) -> s.equals("bar"));
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue("Expectation should fail since message specification failed", failed);
    }

    @Test
    public void expectToThrowWithNoSpecificExceptionShouldFailIfNoExceptionIsThrown() {
        expect(() -> expect(() -> {
        }).toThrow()).toThrow(AssertionError.class);
    }

    @Test
    public void expectToThrowWithNoSpecificExceptionShouldReturnIfExceptionIsThrown() {
        try {
            expect(() -> {
                throw new RuntimeException();
            }).toThrow();
        } catch (AssertionError e) {
            fail("No exception should have occurred");
        }
    }

    @Test
    public void exceptionExpectationCanBeInverted() {
        expect(() -> {
            expect(() -> {
                throw new RuntimeException();
            }).not().toThrow();
        }).toThrow(AssertionError.class);
    }

    @Test
    public void expectationCanBeInverted() {
        expect(() -> expect(true).not().toBe(false)).not().toThrow();
        expect(() -> expect(true).not().toBe(true)).toThrow(AssertionError.class);
    }

    @Test
    public void configureExpectShouldNotAcceptNullConfiguration() {
        expect(() -> Expect.Configuration.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldUpdateConfigurationAndReturnOldConfiguration() {
        final Expect.Configuration newConfiguration = Expect.Configuration
                .newBuilder()
                .configureExpectationFailureHandler(expectationFailureHandler)
                .build();

        final Expect.Configuration oldConfiguration = Expect.Configuration.configure(newConfiguration);

        try {
            expect(Expect.Configuration.get()).toBe(newConfiguration);
            expect(oldConfiguration).not().toBe(equalTo(null));
        } finally {
            Expect.Configuration.configure(oldConfiguration);
        }
    }

    @Test
    public void customMatcherCanFail() {
        expect(() -> expect("foo").toBe(s -> s.length() == 4))
            .toThrow(AssertionError.class)
            .where(e -> e.getMessage().contains("foo"));
    }

    @Test
    public void matchAgainstInstanceShouldContainExpectedAndActualValueInDescription() {
        expect(() -> expect("foo").toBe("bar"))
                .toThrow(AssertionError.class)
                .where(e -> e.getMessage().contains("foo") && e.getMessage().contains("bar"));
    }

    @Test
    public void valueFromAdditionShouldBeDescribed() {
        int m1 = 1;
        int m2 = 2;
        int m3 = 3;

        try {
            expect(m1 + m2 + m3).toBe(1234);
            fail();
        } catch (AssertionError e) {
            expect(e.getMessage()).to((s) -> s.contains("m1 + m2 + m3 => 6"));
            expect(e.getMessage()).to((s) -> s.contains("1234"));
        }
    }

    @Test
    public void messageOnThrownExceptionCanBeSpecified() {
        final RuntimeException e = new RuntimeException("foo");

        expect(() -> { throw e; }).toThrow(RuntimeException.class).withMessage(equalTo("foo"));

        boolean failed = false;

        try {
            expect(() -> { throw e; }).toThrow(RuntimeException.class).withMessage(equalTo("bar"));
        } catch (AssertionError e1) {
            expect(e1.getMessage()).toBe("Expected \"throw e;\" to throw java.lang.RuntimeException withMessage(equalTo(\"bar\"))");
            failed = true;
        }

        expect(failed).toBe(true);
    }

    @Test
    public void expectationFailureHandlerShouldBeCalledWhenExpectedExceptionIsNotThrown() {
        final Expect.Configuration defaultConfiguration = Expect.Configuration
                .configure(Expect.Configuration.newBuilder()
                        .configureExpectationFailureHandler(expectationFailureHandler)
                        .build());

        try {
            expect(this::nop).toThrow(RuntimeException.class);
            fail();
        } catch (AssertionError e) {
            verify(expectationFailureHandler).handleExpectationFailure(argThat(isExpectedExceptionFailure(RuntimeException.class)));
        } finally {
            Expect.Configuration.configure(defaultConfiguration);
        }
    }

    private BaseMatcher<ExpectationFailure> isExpectedExceptionFailure(final Class<? extends Throwable> expectedException) {
        return new BaseMatcher<ExpectationFailure>() {
            @Override
            public boolean matches(Object item) {
                if (!(item instanceof ExpectedExceptionNotThrown)) {
                    return false;
                }

                final ExpectedExceptionNotThrown expectedExceptionNotThrown = (ExpectedExceptionNotThrown) item;

                if (!expectedExceptionNotThrown.getExpectedException().equals(expectedException)) {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("isExpectedExpectationFailure(expectedException=" + expectedException.getName() + ")");
            }
        };
    }

    private void nop() {
    }
}
