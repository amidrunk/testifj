package org.testifj.lang.decompile;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Statement;
import org.testifj.util.Stack;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.AST.eq;
import static org.testifj.lang.model.ModelQueries.equalTo;
import static org.testifj.lang.model.Sequences.emptySequence;
import static org.testifj.lang.model.Sequences.sequenceOf;

@SuppressWarnings("unchecked")
public class DecompilationStateSelectorsTest {

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final Stack stack = mock(Stack.class);

    @Before
    public void setup() {
        when(decompilationContext.getStack()).thenReturn(stack);
    }

    @Test
    public void atLeastOneStatementShouldNotMatchContextWithoutStatements() {
        when(decompilationContext.getStatements()).thenReturn(emptySequence());

        given(DecompilationStateSelectors.atLeastOneStatement()).then(selector -> {
            expect(selector.select(decompilationContext, ByteCode.nop)).toBe(false);
        });
    }

    @Test
    public void atLeastOneStatementShouldMatchContextWithNonEmptyStatements() {
        when(decompilationContext.getStatements()).thenReturn(sequenceOf(mock(Statement.class)));

        given(DecompilationStateSelectors.atLeastOneStatement()).then(selector -> {
            expect(selector.select(decompilationContext, ByteCode.nop)).toBe(true);
        });
    }


    @Test
    public void elementIsStackedShouldNotAcceptNullArg() {
        expect(() -> DecompilationStateSelectors.elementIsStacked(null)).toThrow(AssertionError.class);
    }

    @Test
    public void elementIsStackedShouldNotSelectContextWithEmptyStack() {
        when(stack.isEmpty()).thenReturn(true);

        expect(DecompilationStateSelectors.elementIsStacked(ElementType.CONSTANT).select(decompilationContext, ByteCode.nop)).toBe(false);
    }

    @Test
    public void elementIsStackedShouldNotSelectContextWithIncorrectElement() {
        when(stack.isEmpty()).thenReturn(false);
        when(stack.peek()).thenReturn(AST.cast(constant(1)).to(byte.class));

        expect(DecompilationStateSelectors.elementIsStacked(ElementType.CONSTANT).select(decompilationContext, ByteCode.nop)).toBe(false);
    }

    @Test
    public void elementIsStackedShouldSelectContextWithMatchingElementOnStack() {
        when(stack.isEmpty()).thenReturn(false);
        when(stack.peek()).thenReturn(constant(1));

        expect(DecompilationStateSelectors.elementIsStacked(ElementType.CONSTANT).select(decompilationContext, ByteCode.nop)).toBe(true);
    }

    @Test
    public void stackSizeIsAtLeastShouldNotAcceptZeroOrNegativeCount() {
        expect(() -> DecompilationStateSelectors.stackSizeIsAtLeast(-1)).toThrow(AssertionError.class);
        expect(() -> DecompilationStateSelectors.stackSizeIsAtLeast(0)).toThrow(AssertionError.class);
    }

    @Test
    public void stackSizeIsAtLeastShouldNotMatchStackWithFewerElements() {
        final DecompilationStateSelector selector = DecompilationStateSelectors.stackSizeIsAtLeast(2);

        when(stack.size()).thenReturn(0);
        expect(selector.select(decompilationContext, ByteCode.nop)).toBe(false);

        when(stack.size()).thenReturn(1);
        expect(selector.select(decompilationContext, ByteCode.nop)).toBe(false);

        when(stack.size()).thenReturn(2);
        expect(selector.select(decompilationContext, ByteCode.nop)).toBe(true);

        when(stack.size()).thenReturn(3);
        expect(selector.select(decompilationContext, ByteCode.nop)).toBe(true);
    }

    @Test
    public void elementsAreStackedWithPredicatesShouldNotAcceptNullPredicates() {
        expect(() -> DecompilationStateSelectors.elementsAreStacked((Predicate[]) null));
    }

    @Test
    public void elementsAreStackedWithPredicateShouldNotMatchSmallerStack() {
        when(stack.size()).thenReturn(1);

        expect(DecompilationStateSelectors.elementsAreStacked(equalTo(constant(1)), equalTo(constant(2))).select(decompilationContext, ByteCode.nop)).toBe(false);
    }

    @Test
    public void elementsAreStackedWithPredicatesShouldNotMatchIfAnyPredicateDoesNotMatch() {
        when(stack.size()).thenReturn(2);
        when(stack.tail(Matchers.eq(-2))).thenReturn(Arrays.asList(constant(1), constant(2)));

        expect(DecompilationStateSelectors.elementsAreStacked(equalTo(constant(1)), equalTo(constant(3))).select(decompilationContext, ByteCode.nop)).toBe(false);
    }

    @Test
    public void elementsAreStackedWithPredicatesShouldMatchIfAllPredicatesMatch() {
        when(stack.size()).thenReturn(2);
        when(stack.tail(Matchers.eq(-2))).thenReturn(Arrays.asList(constant(1), constant(2)));

        expect(DecompilationStateSelectors.elementsAreStacked(equalTo(constant(1)), equalTo(constant(2))).select(decompilationContext, ByteCode.nop)).toBe(true);
    }
}