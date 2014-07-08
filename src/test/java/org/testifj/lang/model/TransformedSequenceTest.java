package org.testifj.lang.model;

import org.junit.Test;

import java.util.Arrays;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.IterableThatIs.emptyIterable;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;
import static org.testifj.matchers.core.IteratorThatIs.iteratorOf;

public class TransformedSequenceTest {

    private final Sequence<String> sourceSequence = new LinkedSequence<>();

    private final Sequence<Integer> transformedSequence = new TransformedSequence<>(sourceSequence, Integer::parseInt, Object::toString);

    @Test
    public void enlistShouldNotAcceptNullArg() {
        expect(() -> transformedSequence.add(null)).toThrow(AssertionError.class);
    }

    @Test
    public void enlistShouldEnlistTransformedElementInSourceSeries() {
        transformedSequence.add(1234);
        expect(sourceSequence).toBe(iterableOf("1234"));
    }

    @Test
    public void lastShouldReturnTransformedElementSelectorFromSourceSeries() {
        sourceSequence.add("1234");

        final Sequence.SingleElement<Integer> last = transformedSequence.last();

        expect(last.exists()).toBe(true);
        expect(last.get()).toBe(1234);

        last.swap(2345);
        expect(sourceSequence.last().get()).toBe("2345");
        expect(last.get()).toBe(2345);

        last.remove();

        expect(sourceSequence).toBe(emptyIterable());
    }

    @Test
    public void firstShouldReturnTransformedElementSelectorFromSourceSeries() {
        sourceSequence.add("1111");

        expect(transformedSequence.first().get()).toBe(1111);
        expect(transformedSequence.first().exists()).toBe(true);

        transformedSequence.first().swap(2345);

        expect(sourceSequence.first().get()).toBe("2345");
        expect(transformedSequence.first().get()).toBe(2345);

        transformedSequence.first().remove();

        expect(sourceSequence).toBe(emptyIterable());
    }

    @Test
    public void atIndexShouldReturnTransformedSelectorFromSourceSeries() {
        sourceSequence.add("1");
        sourceSequence.add("2");
        sourceSequence.add("3");

        expect(transformedSequence.at(1).get()).toBe(2);
        expect(transformedSequence.at(1).exists()).toBe(true);

        transformedSequence.at(1).swap(4321);

        expect(sourceSequence.at(1).get()).toBe("4321");
        expect(transformedSequence.at(1).get()).toBe(4321);
        expect(sourceSequence).toBe(iterableOf("1", "4321", "3"));

        transformedSequence.at(1).remove();

        expect(sourceSequence).toBe(iterableOf("1", "3"));
    }

    @Test
    public void allShouldReturnTransformedSelectorFromSourceSeries() {
        sourceSequence.add("1");
        sourceSequence.add("2");

        expect(transformedSequence.all().get()).toBe(collectionOf(1, 2));

        transformedSequence.all().remove();

        expect(sourceSequence).toBe(emptyIterable());
    }

    @Test
    public void sizeShouldBeReturnedFromSourceList() {
        sourceSequence.add("1234");

        expect(transformedSequence.size()).toBe(1);
    }

    @Test
    public void iteratorShouldContainTransformedElements() {
        sourceSequence.add("1");
        sourceSequence.add("2");
        sourceSequence.add("3");

        expect(transformedSequence.iterator()).toBe(iteratorOf(1, 2, 3));
    }

    @Test
    public void clearShouldClearSourceSeries() {
        sourceSequence.add("1234");
        sourceSequence.add("2345");
        transformedSequence.clear();

        expect(sourceSequence).toBe(emptyIterable());
    }

    @Test
    public void isEmptyShouldBeTrueIfSourceSeriesIsEmpty() {
        expect(transformedSequence.isEmpty()).toBe(true);
    }

    @Test
    public void isEmptyShouldBeFalseIfSourceSeriesIsFalse() {
        sourceSequence.add("1234");
        expect(transformedSequence.isEmpty()).toBe(false);
    }

    @Test
    public void firstShouldNotAcceptNullPredicate() {
        expect(() -> transformedSequence.first(null)).toThrow(AssertionError.class);
    }

    @Test
    public void firstShouldReturnFirstMatchingElement() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3", "4"));
        expect(transformedSequence.first(n -> n >= 2).get()).toBe(2);
        expect(transformedSequence.first(n -> n >= 3).get()).toBe(3);
    }

    @Test
    public void lastByPredicateShouldReturnFirstMatchingElementFromEnd() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3", "4"));

        final Sequence.SingleElement<Integer> selector = transformedSequence.last(n -> n <= 3);

        expect(selector.exists()).toBe(true);
        expect(selector.get()).toBe(3);
    }

    @Test
    public void lastByPredicateShouldNotAcceptNullArg() {
        expect(() -> transformedSequence.last(null)).toThrow(AssertionError.class);
    }

    @Test
    public void insertBeforeFirstShouldInsertElementAtFirstIndex() {
        sourceSequence.add("1");
        transformedSequence.first().insertBefore(0);

        expect(sourceSequence).toBe(iterableOf("0", "1"));
    }

    @Test
    public void insertBeforeFirstByPredicateShouldInsertElementBeforeFirstMatchingElement() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));

        transformedSequence.first(n -> n == 2).insertBefore(100);

        expect(sourceSequence).toBe(iterableOf("1", "100", "2", "3"));
    }

    @Test
    public void insertBeforeLastShouldInsertElementSecondToLast() {
        sourceSequence.addAll(Arrays.asList("1", "2"));

        transformedSequence.last().insertBefore(100);

        expect(sourceSequence).toBe(iterableOf("1", "100", "2"));
    }


    @Test
    public void insertBeforeLastByPredicateShouldInsertElementBeforeFirstMatchingFromEnd() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));

        transformedSequence.last(n -> n <= 2).insertBefore(100);

        expect(sourceSequence).toBe(iterableOf("1", "100", "2", "3"));
    }

    @Test
    public void insertBeforeElementAtIndexShouldInsertElementBeforeMatchingIndex() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));

        transformedSequence.at(1).insertBefore(100);

        expect(sourceSequence).toBe(iterableOf("1", "100", "2", "3"));
    }

    @Test
    public void selectorCanBeNavigatedToPreviousElement() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));
        expect(transformedSequence.last().previous().get()).toBe(2);
    }

}