package org.testifj.lang.decompile;

import org.junit.Test;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.Statement;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;
import static org.testifj.matchers.core.OptionalThatIs.present;

public class DecompilationContextQueriesTest {

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    @Test
    public void lastStatementOfShouldReturnNonPresentInstanceForNull() {
        expect(DecompilationContextQueries.lastDecompiledStatement().from(null)).not().toBe(present());
    }

    @Test
    public void lastStatementOfShouldReturnEmptyForEmptyStatementList() {
        when(decompilationContext.getStatements()).thenReturn(Collections.emptyList());

        final Optional<Statement> result = DecompilationContextQueries.lastDecompiledStatement().from(decompilationContext);

        expect(result).not().toBe(present());
    }

    @Test
    public void lastStatementOfShouldReturnLastStatementWhenAvailable() {
        final Statement statement1 = mock(Statement.class, "statement1");
        final Statement statement2 = mock(Statement.class, "statement2");

        when(decompilationContext.getStatements()).thenReturn(Arrays.asList(statement1, statement2));

        final Optional<Statement> result = DecompilationContextQueries.lastDecompiledStatement().from(decompilationContext);

        expect(result).toBe(optionalOf(statement2));
    }

    @Test
    public void previousValueShouldReturnNonPresentResultIfStackIsEmpty() {
        when(decompilationContext.getStackedExpressions()).thenReturn(Collections.emptyList());

        expect(DecompilationContextQueries.previousValue().from(decompilationContext)).not().toBe(present());
    }

    @Test
    public void previousValueShouldReturnNonPresentResultIfContextContainsSingleValue() {
        when(decompilationContext.getStackedExpressions()).thenReturn(Arrays.asList(mock(Expression.class)));

        expect(DecompilationContextQueries.previousValue().from(decompilationContext)).not().toBe(present());
    }

    @Test
    public void previousValueShouldReturnSecondToLastStackedValue() {
        final Expression value1 = mock(Expression.class);
        final Expression value2 = mock(Expression.class);
        final Expression value3 = mock(Expression.class);

        when(decompilationContext.getStackedExpressions()).thenReturn(Arrays.asList(value1, value2, value3));

        expect(DecompilationContextQueries.previousValue().from(decompilationContext)).toBe(optionalOf(value2));
    }

    @Test
    public void previousValueShouldReturnNonPresentResultForNull() {
        expect(DecompilationContextQueries.previousValue().from(null)).not().toBe(present());
    }

    @Test
    public void currentValueShouldReturnNonPresentResultForNull() {
        expect(DecompilationContextQueries.currentValue().from(null)).not().toBe(present());
    }

    @Test
    public void currentValueShouldReturnNonPresentResultIfStackIsEmpty() {
        when(decompilationContext.hasStackedExpressions()).thenReturn(false);

        expect(DecompilationContextQueries.currentValue().from(decompilationContext)).not().toBe(present());
    }

    @Test
    public void currentValueShouldReturnStackedElement() {
        final Expression expectedExpression = mock(Expression.class);

        when(decompilationContext.hasStackedExpressions()).thenReturn(true);
        when(decompilationContext.peek()).thenReturn(expectedExpression);

        expect(DecompilationContextQueries.currentValue().from(decompilationContext)).toBe(optionalOf(expectedExpression));
    }

}