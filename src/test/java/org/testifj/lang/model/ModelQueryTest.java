package org.testifj.lang.model;

import org.junit.Test;

import javax.swing.plaf.nimbus.State;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class ModelQueryTest {

    private final ModelQuery<List<Statement>, Statement> firstInList = from -> from.isEmpty() ? Optional.<Statement>empty() : Optional.of(from.get(0));

    @Test
    public void asShouldNotAcceptNullType() {
        expect(() -> firstInList.as(null)).toThrow(AssertionError.class);
    }

    @Test
    public void asShouldCastQueriedInstance() {
        final VariableAssignment expectedElement = mock(VariableAssignment.class);
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class).from(Arrays.asList(expectedElement));

        expect(result).toBe(optionalOf(expectedElement));
    }

    @Test
    public void asShouldReturnNonPresentOptionalIfTargetIsNonPresent() {
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class).from(Collections.emptyList());

        expect(result).not().toBe(present());
    }

    @Test
    public void asShouldReturnNonPresentOptionalIfTargetIsOfIncorrectType() {
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class).from(Arrays.asList(mock(Statement.class)));

        expect(result).not().toBe(present());
    }

    @Test
    public void whereShouldNotAcceptNullPredicate() {
        expect(() -> firstInList.where(null)).toThrow(AssertionError.class);
    }

    @Test
    public void queryShouldReturnEmptyResultIfPredicateDoesNotMatch() {
        final Optional<Statement> result = firstInList.where(statement -> false).from(Arrays.asList(mock(Statement.class)));

        expect(result).not().toBe(present());
    }

    @Test
    public void queryShouldReturnResultIfPredicateMatches() {
        final Statement expectedStatement = mock(Statement.class);
        final Optional<Statement> result = firstInList.where(statement -> true).from(Arrays.asList(expectedStatement));

        expect(result).toBe(optionalOf(expectedStatement));
    }

    @Test
    public void getShouldNotAcceptNullModelQuery() {
        expect(() -> firstInList.get(null)).toThrow(AssertionError.class);
    }

    @Test
    public void getShouldReturnQueryThatRetrievesFurtherValues() {
        final Statement statement = mock(Statement.class);

        when(statement.getElementType()).thenReturn(ElementType.RETURN_VALUE);

        final Optional<ElementType> result = firstInList
                .get(e -> Optional.of(e.getElementType()))
                .from(Arrays.asList(statement));

        expect(result).toBe(optionalOf(ElementType.RETURN_VALUE));
    }

    @Test
    public void getShouldReturnNonPresentOptionalIfOriginalSourceIsNotPresent() {
        final Optional<ElementType> result = firstInList
                .get(e -> Optional.of(e.getElementType()))
                .from(Collections.emptyList());

        expect(result).not().toBe(present());
    }

    @Test
    public void isShouldNotAcceptNullPredicate() {
        expect(() -> firstInList.is(null)).toThrow(AssertionError.class);
    }

    @Test
    public void isShouldReturnPredicateOnResult() {
        final Predicate<List<Statement>> predicate = firstInList.is(s -> s.getElementType() == ElementType.RETURN);

        expect(predicate.test(Arrays.<Statement>asList(AST.$return(), AST.$return(AST.constant(1))))).toBe(true);
        expect(predicate.test(Arrays.<Statement>asList(AST.$return(AST.constant(1)), AST.$return()))).toBe(false);
    }

    @Test
    public void isShouldNotMatchNonPresentResult() {
        final Predicate<List<Statement>> predicate = firstInList.is(s -> s != null);

        expect(predicate.test(Collections.emptyList())).toBe(false);
    }

    @Test
    public void multipleWhereQueriesCanBeChangedWithAnd() {
        final ModelQuery<List<Statement>, Statement> modelQuery = firstInList
                .where(s -> s.getElementType() == ElementType.RETURN)
                .and(s -> s.getMetaData() != null);

        final Statement statement = mock(Statement.class);
        expect(modelQuery.from(Arrays.asList(statement))).not().toBe(present());

        when(statement.getElementType()).thenReturn(ElementType.RETURN);
        expect(modelQuery.from(Arrays.asList(statement))).not().toBe(present());

        when(statement.getMetaData()).thenReturn(mock(ElementMetaData.class));
        expect(modelQuery.from(Arrays.asList(statement))).toBe(optionalOf(statement));
    }

}