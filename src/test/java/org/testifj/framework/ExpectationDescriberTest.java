package org.testifj.framework;

import io.recode.codegeneration.impl.CodePointerCodeGenerator;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.RecordingTestContextProvider;
import org.testifj.matchers.core.IntegerThatIs;

import static org.junit.Assert.assertEquals;
import static org.testifj.Expectations.expect;
import static org.testifj.matchers.core.IntegerThatIs.lessThan;

public class ExpectationDescriberTest {

    private final ExpectationDescriber describer = new ExpectationDescriber(new CodeLocationDecompilerImpl(), new CodePointerCodeGenerator(), new ValueDescriber());

    private final RecordingTestContextProvider testContextProvider = (RecordingTestContextProvider) TestContextProviders.configuredTestContextProvider();

    @Before
    public void setup() {
        testContextProvider.enforceExpectations(false);
    }

    @After
    public void tearDown() {
        testContextProvider.reset();
    }

    @Test
    public void expectationWithExpectedValueCanBeDescribed() {
        expect("foo").toEqual("bar");

        final Expectation expectation = ValueExpectation.builder()
                .subject("foo")
                .expectedValue("bar")
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new MatchCriterion(value -> true))
                .build();

        assertEquals("expected \"foo\" to equal \"bar\"", describer.describe(expectation).get());
    }

    @Test
    public void negatedExpectationWithValueCanBeDescribed() {
        expect("foo").not().toEqual("bar");

        final Expectation expectation = ValueExpectation.builder()
                .subject("foo")
                .expectedValue("bar")
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new MatchCriterion(value -> true))
                .build();

        assertEquals("expected \"foo\" not to equal \"bar\"", describer.describe(expectation).get());
    }

    @Test
    public void resultOfActualExpressionShouldBeIncluded() {
        final String value = new String("foo");

        expect(value).toEqual("foo");

        final Expectation expectation = ValueExpectation.builder()
                .subject("foo")
                .expectedValue("foo")
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new MatchCriterion(v -> true))
                .build();

        assertEquals("expected [value] => \"foo\" to equal \"foo\"", describer.describe(expectation).get());
    }

    @Test
    public void resultOfExpectedExpressionShouldBeIncludedForSimpleExpectation() {
        final String value = new String("foo");

        expect("foo").toEqual(value);

        final Expectation expectation = ValueExpectation.builder()
                .subject("foo")
                .expectedValue("bar")
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new MatchCriterion(v -> true))
                .build();

        assertEquals("expected \"foo\" to equal [value] => \"bar\"", describer.describe(expectation).get());
    }

    @Test
    public void expectedExceptionWithNoExceptionCanBeDescribed() {
        expect(() -> {}).toThrow(IllegalArgumentException.class);

        final Expectation expectation = BehaviouralExpectation.builder()
                .subject(() -> {})
                .outcome(BehavioralOutcome.successful())
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new ExceptionalBehaviourCriterion(e -> true))
                .build();

        assertEquals("expected [{}] to throw IllegalArgumentException", describer.describe(expectation).get());
    }

    @Test
    public void expectedExceptionWithInvalidExceptionCanBeDescribed() {
        expect(() -> {}).toThrow(IllegalArgumentException.class);

        final Expectation expectation = BehaviouralExpectation.builder()
                .subject(() -> {})
                .outcome(BehavioralOutcome.exceptional(new RuntimeException("anErrorMessage")))
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new ExceptionalBehaviourCriterion(e -> false))
                .build();

        assertEquals("expected [{}] to throw IllegalArgumentException, actually threw RuntimeException(\"anErrorMessage\")", describer.describe(expectation).get());
    }

    @Test
    public void expectedExceptionWithInstanceMethodPointerCanBeDescribed() {
        final Runnable runnable = Mockito.mock(Runnable.class);

        expect(runnable::run).toThrow(IllegalArgumentException.class);

        final Expectation expectation = BehaviouralExpectation.builder()
                .subject(() -> {})
                .outcome(BehavioralOutcome.successful())
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new ExceptionalBehaviourCriterion(e -> true))
                .build();

        assertEquals("expected [runnable::run] to throw IllegalArgumentException", describer.describe(expectation).get());
    }

    @Test
    public void expectedExceptionWithStaticMethodPointerCanBeDescribed() {
        expect(ExpectationDescriberTest::exampleStaticMethod).toThrow(IllegalArgumentException.class);

        final Expectation expectation = BehaviouralExpectation.builder()
                .subject(() -> {})
                .outcome(BehavioralOutcome.successful())
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new ExceptionalBehaviourCriterion(e -> true))
                .build();

        assertEquals("expected [ExpectationDescriberTest::exampleStaticMethod] to throw IllegalArgumentException", describer.describe(expectation).get());
    }

    @Test
    public void dslMatcherShouldBeGeneratedAsNaturalText() {
        expect(10).toBe(lessThan(100));

        final Expectation expectation = ValueExpectation.builder()
                .subject(10)
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -4))
                .criterion(new ExceptionalBehaviourCriterion(e -> true))
                .build();

        final String description = describer.describe(expectation).get();

        assertEquals("expected 10 to be less than [100]", description);
    }

    @Test
    public void dslMatcherWithMultipleConditionsShouldBeGeneratedAsNaturalText() {
        expect(10).toBe(lessThan(100).and(lessThan(1000)));

        final Expectation expectation = ValueExpectation.builder()
                .subject(10)
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -4))
                .criterion(new ExceptionalBehaviourCriterion(e -> true))
                .build();

        final String description = describer.describe(expectation).get();

        assertEquals("expected 10 to be less than [100] and less than [1000]", description);
    }

    @Test
    public void dslCallCanBeUsedInCondition() {
        expect(10).toBe(IntegerThatIs.lessThan(11));

        final Expectation expectation = ValueExpectation.builder()
                .subject(10)
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -4))
                .criterion(new MatchCriterion(e -> true))
                .build();

        final String description = describer.describe(expectation).get();

        assertEquals("expected 10 to be less than [11]", description);
    }

    @Test
    public void valueOfExpectedValueShouldBeRendered() {
        final String str1 = new String("foo");
        final String str2 = new String("foo");

        expect(str1).toEqual(str2);

        final Expectation expectation = ValueExpectation.builder()
                .subject(str1)
                .expectedValue("foo")
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new MatchCriterion(e -> true))
                .build();

        final String description = describer.describe(expectation).get();

        assertEquals("expected [str1] => \"foo\" to equal [str2] => \"foo\"", description);
    }

    @Test
    public void actualValueShouldNotBeRenderedIfRepresentationIsStupid() {
        final Object o = new Object();

        expect(o).not().toBeNull();

        final Expectation expectation = ValueExpectation.builder()
                .subject(o)
                .expectedValue(o)
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -5))
                .criterion(new MatchCriterion(e -> true))
                .build();

        final String description = describer.describe(expectation).get();

        assertEquals("expected [o] not to be null", description);
    }

    static void exampleStaticMethod() {}
}