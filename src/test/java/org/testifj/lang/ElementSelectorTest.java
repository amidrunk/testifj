package org.testifj.lang;

import org.junit.Test;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.decompile.ElementSelector;
import org.testifj.lang.decompile.impl.CodePointerImpl;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.ElementType;

import java.util.function.Predicate;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

@SuppressWarnings("unchecked")
public class ElementSelectorTest {

    private final Method method = mock(Method.class);

    @Test
    public void forTypeShouldNotAcceptNullElementType() {
        expect(() -> ElementSelector.forType(null)).toThrow(AssertionError.class);
    }

    @Test
    public void selectorForTypeShouldMatchEqualElementType() {
        final ElementSelector<Constant> selector = ElementSelector.<Constant>forType(ElementType.CONSTANT);
        final CodePointer<Constant> codePointer = new CodePointerImpl<>(mock(Method.class), AST.constant(1));

        expect(selector.matches(codePointer)).toBe(true);
    }

    @Test
    public void selectorForTypeShouldNotMatchDifferentElementType() {
        final ElementSelector<Constant> selector = ElementSelector.<Constant>forType(ElementType.CONSTANT);
        final CodePointer codePointer = new CodePointerImpl<>(method, AST.$return());

        expect(selector.matches(codePointer)).toBe(false);
    }

    @Test
    public void elementSelectorForTypeShouldNotAcceptNullCodePointerWhenMatching() {
        expect(() -> ElementSelector.forType(ElementType.CONSTANT).matches(null)).toThrow(AssertionError.class);
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldNotCallPredicateIfElementTypeIsIncorrect() {
        final Predicate predicate = mock(Predicate.class);
        final ElementSelector selector = ElementSelector.forType(ElementType.CONSTANT).where(predicate);

        expect(selector.matches(new CodePointerImpl<>(method, AST.$return()))).toBe(false);

        verifyZeroInteractions(predicate);
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldNotAcceptNullPredicate() {
        expect(() -> ElementSelector.forType(ElementType.CONSTANT).where(null)).toThrow(AssertionError.class);
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldNotMatchIfPredicateDoesNotMatch() {
        final Predicate predicate = mock(Predicate.class);
        final ElementSelector selector = ElementSelector.forType(ElementType.CONSTANT).where(predicate);
        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, AST.constant(1));

        when(predicate.test(eq(codePointer))).thenReturn(false);

        expect(selector.matches(codePointer)).toBe(false);

        verify(predicate).test(eq(codePointer));
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldMatchIfPredicateMatches() {
        final Predicate predicate = mock(Predicate.class);
        final ElementSelector selector = ElementSelector.forType(ElementType.CONSTANT).where(predicate);
        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, AST.constant(1));

        when(predicate.test(eq(codePointer))).thenReturn(true);

        expect(selector.matches(codePointer)).toBe(true);

        verify(predicate).test(eq(codePointer));
    }

}
