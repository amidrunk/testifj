package org.testifj.lang.model;

import org.junit.Test;
import org.testifj.lang.model.impl.ConstantExpressionImpl;

import static org.testifj.Expect.expect;
import static org.testifj.lang.model.AST.constant;

public class ASTTest {

    @Test
    public void constantStringCannotBeNull() {
        expect(() -> constant(null)).toThrow(AssertionError.class);
    }

    @Test
    public void stringConstantCanBeCreated() {
        expect(constant("foo")).toBe(new ConstantExpressionImpl("foo", String.class));
    }

    @Test
    public void intConstantCanBeCreated() {
        expect(constant(1)).toBe(new ConstantExpressionImpl(1, int.class));
    }

}
