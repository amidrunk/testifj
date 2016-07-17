package org.testifj.framework.codegeneration;

import org.junit.Test;
import org.testifj.Procedure;
import org.testifj.framework.*;
import org.testifj.matchers.core.ObjectThatIs;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.testifj.Expectations.expect;

public class DefaultExpectationEmitterFactoryTest {

    private final DefaultExpectationEmitterFactory factory = new DefaultExpectationEmitterFactory();

    @Test
    public void emitterCannotBeCreatedFromUnsupportedExpectationType() {
        expect(() -> factory.createContext(mock(Expectation.class))).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void emitterCannotBeCreatedFromUnsupportedExpectationReference() {
        final ValueExpectation expectation = ValueExpectation.builder()
                .criterion(mock(Criterion.class))
                .subject("foo")
                .expectationReference(mock(ExpectationReference.class))
                .build();

        expect(() -> factory.createContext(expectation)).toThrow(IllegalArgumentException.class);
    }

    @Test
    public void contextCanBeCreatedForValueExpectation() {
        expect("foo").toEqual("foo"); // sample expectation

        final ValueExpectation expectation = ValueExpectation.builder()
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -3))
                .subject("foo")
                .criterion(new MatchCriterion(ObjectThatIs.equalTo("foo")))
                .expectedValue("foo")
                .build();

        final ExpectationEmitter emitter = factory.createContext(expectation);

        expect(emitter.getExpectation()).toEqual(expectation);
        expect(emitter.getMethodCalls()).toContainExactly(
                m -> m.getElement().getMethodName().equals("expect"),
                m -> m.getElement().getMethodName().equals("toEqual")
        );
    }

    @Test
    public void contextCanBeCreatedForBehavioralExpectation() {
        expect(() ->  { throw new IllegalArgumentException(); }).toThrow(IllegalArgumentException.class); // sample expectation

        final BehaviouralExpectation expectation = BehaviouralExpectation.builder()
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -3))
                .subject(mock(Procedure.class))
                .criterion(new ExceptionalBehaviourCriterion(ObjectThatIs.instanceOf(IllegalArgumentException.class)))
                .outcome(BehavioralOutcome.successful())
                .build();

        final ExpectationEmitter emitter = factory.createContext(expectation);

        expect(emitter.getExpectation()).toEqual(expectation);
        expect(emitter.getMethodCalls()).toContainExactly(
                m -> m.getElement().getMethodName().equals("expect"),
                m -> m.getElement().getMethodName().equals("toThrow")
        );
    }

    @Test
    public void codeCanBeEmittedAndRetrieved() {
        final ExpectationEmitter emitter = exampleEmitter();

        emitter.emit("foo");
        emitter.emit("bar");
        expect(emitter.collect()).toEqual("foobar");

        emitter.out().append("baz");
        expect(emitter.collect()).toEqual("foobarbaz");
    }

    private ExpectationEmitter exampleEmitter() {
        expect("foo").toEqual("foo"); // sample expectation

        final ValueExpectation expectation = ValueExpectation.builder()
                .expectationReference(InlineExpectationReference.create(Thread.currentThread().getStackTrace(), 1, -3))
                .subject("foo")
                .criterion(new MatchCriterion(ObjectThatIs.equalTo("foo")))
                .expectedValue("foo")
                .build();

        return factory.createContext(expectation);
    }
}