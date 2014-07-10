package org.testifj.lang.model.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.model.*;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.StringShould.containString;

public class IncrementImplTest {

    private final Increment exampleIncrement = new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), int.class, Affix.UNDEFINED);

    @Test
    public void constructorShouldNotAcceptNullLocalVariableOrValue() {
        expect(() -> new IncrementImpl(null, AST.constant(1), int.class, Affix.UNDEFINED)).toThrow(AssertionError.class);
        expect(() -> new IncrementImpl(AST.local("foo", int.class, 1), null, int.class, Affix.UNDEFINED)).toThrow(AssertionError.class);
        expect(() -> new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), null, Affix.UNDEFINED)).toThrow(AssertionError.class);
        expect(() -> new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), int.class, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        given(exampleIncrement).then(it -> {
            expect(it.getElementType()).toBe(ElementType.INCREMENT);
            expect(it.getType()).toBe(int.class);
            expect(it.getLocalVariable()).toBe(AST.local("foo", int.class, 1));
            expect(it.getValue()).toBe(AST.constant(1));
            expect(it.getAffix()).toBe(Affix.UNDEFINED);
        });
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        expect(exampleIncrement).toBe(equalTo(exampleIncrement));
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        expect(exampleIncrement).not().toBe(equalTo(null));
        expect((Object) exampleIncrement).not().toBe(equalTo("foo"));
    }

    @Test
    public void instancesWithEqualOperandsShouldBeEqual() {
        final Increment other = new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), int.class, Affix.UNDEFINED);

        expect(exampleIncrement).toBe(equalTo(other));
        expect(exampleIncrement.hashCode()).toBe(equalTo(other.hashCode()));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        expect(exampleIncrement.toString()).to(containString(AST.local("foo", int.class, 1).toString()));
        expect(exampleIncrement.toString()).to(containString(AST.constant(1).toString()));
        expect(exampleIncrement.toString()).to(containString(Affix.UNDEFINED.toString()));
    }

    @Test
    public void incrementWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        expect(new IncrementImpl(mock(LocalVariableReference.class), mock(Expression.class), String.class, Affix.POSTFIX, metaData).getMetaData()).toBe(metaData);
        expect(exampleIncrement.getMetaData()).not().toBe(equalTo(null));
    }

}