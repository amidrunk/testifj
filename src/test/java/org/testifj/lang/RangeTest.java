package org.testifj.lang;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    @Test
    public void rangeCanBeCreatedFromDSL() {
        final Range range = Range.from(5).to(10);

        expect(range.getFrom()).toBe(5);
        expect(range.getTo()).toBe(10);
    }

    @Test
    public void rangeElementsCanBeRetrievedFromRange() {
        final Range range = Range.from(1).to(3);

        expect(range.all()).toBe(new int[] {1, 2, 3});
    }

    @Test
    public void eachShouldNotAcceptNullConsumer() {
        expect(() -> Range.from(1).to(2).each(null)).toThrow(AssertionError.class);
    }

    @Test
    public void eachShouldVisitAllElements() {
        final Range range = Range.from(1).to(2);
        final IntConsumer consumer = mock(IntConsumer.class);

        range.each(consumer);

        final InOrder inOrder = Mockito.inOrder(consumer);

        inOrder.verify(consumer).accept(eq(1));
        inOrder.verify(consumer).accept(eq(2));
    }

    @Test
    public void collectShouldNotAcceptNullMapFunction() {
        expect(() -> Range.from(1).to(2).collect(null)).toThrow(AssertionError.class);
    }

    @Test
    public void collectShouldReturnTransformedList() {
        final Range range = Range.from(1).to(2);
        final IntFunction intFunction = mock(IntFunction.class);

        when(intFunction.apply(eq(1))).thenReturn("foo");
        when(intFunction.apply(eq(2))).thenReturn("bar");

        final List result = range.collect(intFunction);

        expect(result.toArray()).toBe(new Object[]{"foo", "bar"});
    }

    @Test
    public void allAsListShouldReturnElementsAsList() {
        final List<Integer> list = Range.from(1).to(2).allAsList();

        expect(list.toArray()).toBe(new Object[]{1, 2});
    }
}
