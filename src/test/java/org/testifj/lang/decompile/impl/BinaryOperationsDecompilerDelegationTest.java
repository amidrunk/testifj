package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerDelegate;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.*;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class BinaryOperationsDecompilerDelegationTest {

    private final BinaryOperationsDecompilerDelegation delegation = new BinaryOperationsDecompilerDelegation();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    @Test
    public void configureShouldNotAcceptNullArg() {
        expect(() -> delegation.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void supportForArithmeticIntegerOperatorsShouldBeConfigured() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iadd)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.isub)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.imul)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.idiv)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void iaddShouldPushAdditionOfStackedOperands() {
        given(extension(ByteCode.iadd)).then(it -> {
            when(decompilationContext.pop()).thenReturn(constant(2), constant(1));

            it.apply(decompilationContext, mock(CodeStream.class), ByteCode.iadd);

            verify(decompilationContext).push(eq(add(constant(1), constant(2), int.class)));
        });
    }

    @Test
    public void isubShouldPushSubtractionOfOperands() {
        given(extension(ByteCode.isub)).then(it -> {
            when(decompilationContext.pop()).thenReturn(constant(2), constant(1));

            it.apply(decompilationContext, mock(CodeStream.class), ByteCode.iadd);

            verify(decompilationContext).push(eq(sub(constant(1), constant(2), int.class)));
        });
    }

    @Test
    public void imulShouldPushMultiplicationOfOperands() {
        given(extension(ByteCode.imul)).then(it -> {
            when(decompilationContext.pop()).thenReturn(constant(2), constant(1));

            it.apply(decompilationContext, mock(CodeStream.class), ByteCode.imul);

            verify(decompilationContext).push(eq(mul(constant(1), constant(2), int.class)));
        });
    }

    @Test
    public void idivShouldPushDivisionOfOperands() {
        given(extension(ByteCode.idiv)).then(it -> {
            when(decompilationContext.pop()).thenReturn(constant(2), constant(1));

            it.apply(decompilationContext, mock(CodeStream.class), ByteCode.idiv);

            verify(decompilationContext).push(eq(div(constant(1), constant(2), int.class)));
        });
    }

    private DecompilerDelegate extension(int instruction) {
        return configuration().getDecompilerExtension(decompilationContext, instruction);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfiguration.Builder builder = new DecompilerConfigurationImpl.Builder();
        delegation.configure(builder);
        return builder.build();
    }
}