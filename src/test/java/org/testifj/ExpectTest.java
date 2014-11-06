package org.testifj;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Ignore;
import org.junit.Test;
import org.testifj.lang.classfile.ConstantPoolEntry;
import org.testifj.lang.classfile.impl.DefaultConstantPool;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.ObjectThatIs.instanceOf;
import static org.testifj.matchers.core.StringShould.containString;
import static org.testifj.matchers.core.StringThatIs.stringContaining;

public class ExpectTest {

    private final ExpectationFailureHandler expectationFailureHandler = mock(ExpectationFailureHandler.class);

    private Object asObject(Object object) {
        return object;
    }

    @Test
    public void expectShouldAcceptMatcherForMoreSpecificTypeThanValue() {
        expect(() -> expect(asObject(new String("str").toString())).toBe(equalTo("str"))).not().toThrow(AssertionError.class);
        expect(() -> expect(asObject(new String("str").toString())).toBe(instanceOf(String.class))).not().toThrow(AssertionError.class);
        expect(() -> expect(asObject(Arrays.asList("foo", "bar"))).toBe(collectionOf("foo", "bar"))).not().toThrow(AssertionError.class);
    }

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
        boolean failed = false;
        String str = "foo";

        try {
            expect(str).toBe(s -> s.length() == 4);
        } catch (AssertionError e) {
            failed = true;

            expect(e.getMessage()).toBe("Expected str => \"foo\" to be s -> s.length() == 4");
        }

        expect(failed).toBe(true);
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
    @Ignore("Reintroduce and fix later")
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

    @Test
    public void testStuff() {
        final String str = "str";

        boolean failed = false;

        try {
            final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                    .addEntry(new ConstantPoolEntry.UTF8Entry("foo"))
                    .create();

            expect(() -> constantPool.getString(1)).toThrow(IllegalArgumentException.class);
        } catch (AssertionError e) {
            expect(e.getMessage()).to(containString("() -> constantPool.getString(1)"));
            expect(e.getMessage()).to(containString("IllegalArgumentException"));
            failed = true;
        }

        expect(failed).toBe(true);
    }

    @Test
    public void withCauseShouldFailIfMatcherFails() {
        final Exception cause = new RuntimeException();
        final Matcher matcher = mock(Matcher.class);

        when(matcher.matches(eq(cause))).thenReturn(false);

        boolean failed = true;

        try {
            expect(() -> {
                throw new RuntimeException(cause);
            }).toThrow(RuntimeException.class).withCause(matcher);

            failed = false;
        } catch (AssertionError e) {
        }

        expect(failed).toBe(true);
        verify(matcher).matches(cause);
    }

    @Test
    public void withCauseShouldSucceedIfMatcherMatches() {
        final Exception cause = new RuntimeException();
        final Matcher matcher = mock(Matcher.class);

        when(matcher.matches(eq(cause))).thenReturn(true);

        expect(() -> {
            throw new RuntimeException(cause);
        }).toThrow(RuntimeException.class).withCause(matcher);

        verify(matcher).matches(eq(cause));
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
