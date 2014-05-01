package org.testifj.lang;

import org.junit.Test;

import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class RangeTest {

    private final Range exampleRange = new Range(1, 10);

    @Test
    public void fromCannotBeGreaterThanTo() {
        expect(() -> new Range(1, 0)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainLimits() {
        given(exampleRange).then(it -> {
            expect(it.getFrom()).toBe(1);
            expect(it.getTo()).toBe(10);
        });
    }

    @Test
    public void rangeShouldBeEqualToItSelf() {
        expect(exampleRange).toBe(equalTo(exampleRange));
    }

    @Test
    public void rangeShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleRange).not().toBe(equalTo(null));
        expect((Object) exampleRange).not().toBe(equalTo("foo"));
    }

    @Test
    public void rangesWithEqualPropertiesShouldBeEqual() {
        final Range other = new Range(1, 10);

        expect(exampleRange).toBe(equalTo(other));
        expect(exampleRange.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainLimits() {
        expect(exampleRange.toString()).toBe("[1, 10]");
    }

}
