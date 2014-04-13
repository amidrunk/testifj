package org.testifj;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testifj.Expect.expect;

public class ExpectTest {

    @Test
    public void expectToThrowShouldSucceedIfConditionsAreFulfilled() {
        expect(() -> {
            throw new IllegalArgumentException("foo");
        }).
                toThrow(IllegalArgumentException.class).
                where((e) -> e.getMessage().equals("foo"));
    }

    @Test
    public void expectToThrowShouldFailIfWhereSpecificationIsNotFulfilled() {
        boolean failed = false;

        try {
            expect(() -> {
                throw new IllegalArgumentException("foo");
            }).
                    toThrow(IllegalArgumentException.class).
                    where((e) -> e.getMessage().equals("bar"));
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
            }).
                    toThrow(IllegalArgumentException.class);
        } catch (AssertionError e) {
            fail();
        }
    }

    @Test
    public void expectToThrowShouldFailIfNoExceptionIsThrown() {
        boolean failed = false;

        try {
            expect(() -> {
            }).toThrow(IllegalArgumentException.class);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue("Specification should fail if no exception is thrown", failed);
    }

    @Test
    public void expectToThrowCanBeCreatedWithMessageMatcher() {
        expect(() -> {
            throw new IllegalArgumentException("foo");
        }).
                toThrow(IllegalArgumentException.class).
                withMessage((s) -> s.equals("foo"));
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


}
