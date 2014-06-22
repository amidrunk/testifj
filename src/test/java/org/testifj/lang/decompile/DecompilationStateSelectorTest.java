package org.testifj.lang.decompile;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class DecompilationStateSelectorTest {

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    @Test
    public void andShouldNotAcceptNullArg() {
        expect(() -> DecompilationStateSelector.ALL.and(null)).toThrow(AssertionError.class);
    }

    @Test
    public void andShouldSelectStateIfBothSelectorsMatches() {
        final DecompilationStateSelector selector1 = (context, byteCode) -> true;
        final DecompilationStateSelector selector2 = (context, byteCode) -> true;

        expect(selector1.and(selector2).select(decompilationContext, ByteCode.nop)).toBe(true);
    }

    @Test
    public void andShouldNotSelectStateIfAnySelectorDoesNotMatch() {
        final DecompilationStateSelector selector1 = (context, byteCode) -> true;
        final DecompilationStateSelector selector2 = (context, byteCode) -> false;

        expect(selector1.and(selector2).select(decompilationContext, ByteCode.nop)).toBe(false);
        expect(selector2.and(selector1).select(decompilationContext, ByteCode.nop)).toBe(false);
    }
}