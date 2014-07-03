package org.testifj.lang.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.CollectionThatIs.empty;
import static org.testifj.matchers.core.IterableThatIs.emptyIterable;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;

public class LinkedSequenceTest {

    private final Sequence<Statement> sequence = new LinkedSequence<>();
    private final Statement statement1 = mock(Statement.class, "statement1");
    private final Statement statement2 = mock(Statement.class, "statement2");
    private final Statement statement3 = mock(Statement.class, "statement3");

    @Test
    public void enlistShouldNotAcceptNullArgument() {
        expect(() -> sequence.add(null)).toThrow(AssertionError.class);
    }

    @Test
    public void allShouldReturnAllEnlistedStatements() {
        sequence.add(statement1);
        sequence.add(statement2);

        expect(sequence.all().get()).toBe(collectionOf(statement1, statement2));
    }

    @Test
    public void allCanClearEntireContents() {
        sequence.add(statement1);
        sequence.add(statement2);

        sequence.all().remove();

        expect(sequence.all().get()).toBe(empty());
    }

    @Test
    public void swapOnFirstElementShouldFailIfStatementsAreEmpty() {
        expect(() -> sequence.first().swap(statement1)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void firstElementCanBeSwapped() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.first().swap(statement3);

        expect(sequence).toBe(iterableOf(statement3, statement2));
    }

    @Test
    public void statementsCanBeIterated() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);

        expect(sequence).toBe(iterableOf(statement1, statement2, statement3));
    }

    @Test
    public void delistFirstShouldFailIfStatementsAreEmpty() {
        expect(() -> sequence.first().remove()).toThrow(NoSuchElementException.class);
    }

    @Test
    public void firstElementCanBeDelisted() {
        sequence.add(statement1);
        sequence.add(statement2);

        expect(sequence.size()).toBe(2);
        expect(sequence).toBe(iterableOf(statement1, statement2));

        sequence.first().remove();

        expect(sequence.size()).toBe(1);
        expect(sequence).toBe(iterableOf(statement2));
    }

    @Test
    public void getLastElementShouldFailOnEmptyStatements() {
        expect(() -> sequence.last().get()).toThrow(NoSuchElementException.class);
    }

    @Test
    public void getLastShouldReturnLastElement() {
        sequence.add(statement1);
        sequence.add(statement2);

        expect(sequence.last().get()).toBe(statement2);
    }

    @Test
    public void swapLastShouldFailIfStatementsAreEmpty() {
        expect(() -> sequence.last().swap(statement2)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void swapLastShouldSwapLastElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.last().swap(statement3);

        expect(sequence).toBe(iterableOf(statement1, statement3));
    }

    @Test
    public void delistLastShouldFailIfStatementsAreEmpty() {
        expect(() -> sequence.last().remove()).toThrow(NoSuchElementException.class);
    }

    @Test
    public void delistLastShouldRemoveLastElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.last().remove();

        expect(sequence.size()).toBe(1);
        expect(sequence).toBe(iterableOf(statement1));
    }

    @Test
    public void getAtIndexShouldFailIfElementDoesNotExist() {
        expect(() -> sequence.at(0).get()).toThrow(NoSuchElementException.class);
    }

    @Test
    public void getAtIndexShouldReturnElementAtIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);

        expect(sequence.at(0).get()).toBe(statement1);
        expect(sequence.at(1).get()).toBe(statement2);
        expect(sequence.at(2).get()).toBe(statement3);
    }

    @Test
    public void statementAtIndexShouldFailImmediatelyForNegativeIndex() {
        expect(() -> sequence.at(-1)).toThrow(AssertionError.class);
    }

    @Test
    public void firstCanBeDelistedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(0).remove();

        expect(sequence.size()).toBe(1);
        expect(sequence).toBe(iterableOf(statement2));
    }

    @Test
    public void lastCanBeReplacedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(1).remove();

        expect(sequence.size()).toBe(1);
        expect(sequence).toBe(iterableOf(statement1));
    }

    @Test
    public void intermediateCanBeReplacedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);
        sequence.at(1).remove();

        expect(sequence.size()).toBe(2);
        expect(sequence).toBe(iterableOf(statement1, statement3));
    }

    @Test
    public void firstElementCanBeSwappedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(0).swap(statement3);

        expect(sequence).toBe(iterableOf(statement3, statement2));
    }

    @Test
    public void lastElementCanBeSwappedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(1).swap(statement3);

        expect(sequence).toBe(iterableOf(statement1, statement3));
    }

    @Test
    public void swapByIndexShouldNotAcceptNullNewStatement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);

        expect(() -> sequence.at(0).swap(null)).toThrow(AssertionError.class);
        expect(() -> sequence.at(1).swap(null)).toThrow(AssertionError.class);
        expect(() -> sequence.at(2).swap(null)).toThrow(AssertionError.class);
    }

    @Test
    public void firstShouldNotExistForEmptyStatements() {
        expect(sequence.first().exists()).toBe(false);
    }

    @Test
    public void firstShouldExistForNonEmptyStatements() {
        sequence.add(statement1);
        expect(sequence.first().exists()).toBe(true);
    }

    @Test
    public void lastShouldNotExistForEmptyStatements() {
        expect(sequence.last().exists()).toBe(false);
    }

    @Test
    public void lastShouldExistForNonEmptyStatements() {
        sequence.add(statement1);
        expect(sequence.last().exists()).toBe(true);
    }

    @Test
    public void getByIndexShouldNotExistsIfNoElementExistsForIndex() {
        sequence.add(statement1);

        expect(sequence.at(1).exists()).toBe(false);
        expect(sequence.at(2).exists()).toBe(false);
    }

    @Test
    public void getByIndexShouldExistIfElementExistsAtIndex() {
        sequence.add(statement1);
        sequence.add(statement2);

        expect(sequence.at(0).exists()).toBe(true);
        expect(sequence.at(1).exists()).toBe(true);
    }

    @Test
    public void clearShouldRemoveElementsAndResetSize() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.clear();

        expect(sequence.size()).toBe(0);
        expect(sequence).toBe(emptyIterable());
    }

    @Test
    public void isEmptyShouldBeTrueIfNoElementsExists() {
        expect(sequence.isEmpty()).toBe(true);
    }

    @Test
    public void isEmptyShouldBeFalseIfAtLeastOneElementExists() {
        sequence.add(statement1);
        expect(sequence.isEmpty()).toBe(false);
    }

    @Test
    public void firstShouldNotAcceptNullPredicate() {
        expect(() -> sequence.first(null)).toThrow(AssertionError.class);
    }

    @Test
    public void firstShouldNotExistIfNoMatchingElementsExists() {
        sequence.add(statement1);
        expect(sequence.first(s -> false).exists()).toBe(false);
    }

    @Test
    public void firstShouldReturnFirstMatchingElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        expect(sequence.first(s -> true).get()).toBe(statement1);
    }

    @Test
    public void firstWithPredicateShouldFailIfListIsConcurrentlyModified() {
        sequence.add(statement1);

        final Sequence.SingleElement<Statement> selector = sequence.first(s -> true);
        expect(selector.exists()).toBe(true);

        sequence.add(statement2);

        expect(() -> selector.get()).toThrow(ConcurrentModificationException.class);
    }

    @Test
    public void removeAtIndexShouldReduceSizeForOneElement() {
        sequence.add(statement1);
        sequence.at(0).remove();

        expect(sequence.size()).toBe(0);
    }

    @Test
    public void removeAtLastIndexShouldReduceSizeForMultipleElements() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);
        sequence.at(2).remove();

        expect(sequence.size()).toBe(2);
    }

    @Test
    public void iteratorShouldFailIfListIsModified() {
        sequence.add(statement1);
        sequence.add(statement2);

        final Iterator<Statement> iterator = sequence.iterator();

        expect(iterator.next()).toBe(statement1);
        sequence.add(statement3);

        expect(() -> iterator.next()).toThrow(ConcurrentModificationException.class);
    }

    @Test
    public void firstWithPredicateCannotBeSwappedIfNoElementMatches() {
        sequence.add(statement1);
        expect(() -> sequence.first(s -> false).swap(statement2)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void firstWithPredicateShouldSwapFirstMatchingElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.first(s -> true).swap(statement3);

        expect(sequence).toBe(iterableOf(statement3, statement2));
    }

    @Test
    public void swapForFirstWithPredicateShouldNotAcceptNullElement() {
        sequence.add(statement1);
        expect(() -> sequence.first(s -> true).swap(null)).toThrow(AssertionError.class);
    }

    @Test
    public void removeForFirstWithPredicateShouldFailIfNoElementExists() {
        sequence.add(statement1);
        expect(() -> sequence.first(s -> false).remove()).toThrow(NoSuchElementException.class);
    }

    @Test
    public void removeForFirstWithPredicateShouldRemoveFirstMatchingElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.first(s -> true).remove();

        expect(sequence.size()).toBe(1);
        expect(sequence).toBe(iterableOf(statement2));
    }

    @Test
    public void lastByPredicateShouldNotAcceptNullPredicate() {
        expect(() -> sequence.last(null)).toThrow(AssertionError.class);
    }

    @Test
    public void lastByPredicateShouldReturnNonExistingSelectorIfNoElementMatches() {
        sequence.addAll(Arrays.asList(statement1, statement2));

        final Sequence.SingleElement<Statement> selector = sequence.last(s -> false);

        expect(selector.exists()).toBe(false);
        expect(() -> selector.get()).toThrow(NoSuchElementException.class);
        expect(() -> selector.swap(statement3)).toThrow(NoSuchElementException.class);
        expect(() -> selector.remove()).toThrow(NoSuchElementException.class);
    }

    @Test
    public void lastByPredicateShouldReturnSelectorForLastMatchingElement() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        final Sequence.SingleElement<Statement> selector = sequence.last(s -> s == statement2);

        expect(selector.exists()).toBe(true);
        expect(selector.get()).toBe(statement2);

        selector.swap(statement3);

        expect(selector.get()).toBe(statement3);
        expect(sequence).toBe(iterableOf(statement1, statement3, statement3));

        selector.remove();
        expect(sequence.size()).toBe(2);
        expect(sequence).toBe(iterableOf(statement1, statement3));
    }

    @Test
    public void insertBeforeFirstShouldInsertElementAtFirstIndex() {
        sequence.addAll(Arrays.asList(statement1, statement2));

        final Sequence.SingleElement<Statement> selector = sequence.first();

        selector.insertBefore(statement3);

        expect(sequence).toBe(iterableOf(statement3, statement1, statement2));
        expect(sequence.size()).toBe(3);
        expect(selector.get()).toBe(statement3);
        expect(sequence.first().get()).toBe(statement3);
    }

    @Test
    public void insertBeforeFirstShouldFailForEmptySequence() {
        expect(() -> sequence.first().insertBefore(statement1)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void insertBeforeFirstShouldNotAcceptNullElement() {
        sequence.add(statement1);
        expect(() -> sequence.first().insertBefore(null)).toThrow(AssertionError.class);
    }

    @Test
    public void insertBeforeFirstByPredicateShouldNotAcceptNullElement() {
        sequence.add(statement1);
        expect(() -> sequence.first(s -> true).insertBefore(null)).toThrow(AssertionError.class);
    }

    @Test
    public void insertBeforeFirstByPredicateShouldFailForEmptySequence() {
        expect(() -> sequence.first(s -> true).insertBefore(statement1)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void insertBeforeFirstByPredicateShouldElementBeforeFirstMatchingElement() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        sequence.first(s -> s == statement2).insertBefore(statement3);

        expect(sequence).toBe(iterableOf(statement1, statement3, statement2, statement3));
    }

    @Test
    public void insertBeforeLastShouldNotAcceptNullElement() {
        sequence.add(statement1);
        expect(() -> sequence.last().insertBefore(null)).toThrow(AssertionError.class);
    }

    @Test
    public void insertBeforeLastShouldFailForEmptySequence() {
        expect(() -> sequence.last().insertBefore(statement1)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void insertBeforeLastShouldInsertElementBeforeLastElement() {
        sequence.add(statement1);

        final Sequence.SingleElement<Statement> selector = sequence.last();

        selector.insertBefore(statement2);

        expect(sequence).toBe(iterableOf(statement2, statement1));

        selector.insertBefore(statement3);

        expect(sequence).toBe(iterableOf(statement2, statement3, statement1));
    }

    @Test
    public void insertAtLastByPredicateShouldNotAcceptNullElement() {
        sequence.add(statement1);
        expect(() -> sequence.last(s -> s == statement1).insertBefore(null)).toThrow(AssertionError.class);
    }

    @Test
    public void insertAtLastByPredicateShouldFailIfNoElementMatches() {
        sequence.add(statement1);
        expect(() -> sequence.last(s -> false).insertBefore(statement3)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void insertAtLastByPredicateShouldInsertElementBeforeFirstMatchingElementFromEnd() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        final Sequence.SingleElement<Statement> selector = sequence.last(s -> s == statement2);

        selector.insertBefore(statement3);

        expect(sequence).toBe(iterableOf(statement1, statement3, statement2, statement3));
    }

    @Test
    public void insertBeforeSpecifiedIndexShouldFailForInvalidIndex() {
        expect(() -> sequence.at(0).insertBefore(statement1)).toThrow(NoSuchElementException.class);
    }

    @Test
    public void insertBeforeSpecifiedIndexShouldNotAcceptNullElement() {
        sequence.add(statement1);
        expect(() -> sequence.at(0).insertBefore(null)).toThrow(AssertionError.class);
    }

    @Test
    public void insertBeforeShouldInsertElementAtIndexAndUpdateSelectorValue() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        final Sequence.SingleElement<Statement> selector = sequence.at(1);

        selector.insertBefore(statement3);

        expect(sequence).toBe(iterableOf(statement1, statement3, statement2, statement3));
        expect(selector.get()).toBe(statement3);
    }

}