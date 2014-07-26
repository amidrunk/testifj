package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.CodeStreamTestUtils;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerConfigurationBuilder;
import org.testifj.lang.model.Expression;
import org.testifj.util.SingleThreadedStack;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.*;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class BinaryOperationsTest {

    private final BinaryOperations delegation = new BinaryOperations();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final SingleThreadedStack<Expression> stack = new SingleThreadedStack<>();

    @Before
    public void setup() {
        when(decompilationContext.getStack()).thenReturn(stack);
    }

    @Test
    public void configureShouldNotAcceptNullArg() {
        expect(() -> delegation.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void supportForArithmeticIntegerOperatorsShouldBeConfigured() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.iadd)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.isub)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.imul)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.idiv)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void supportForAllArithmeticFloatOperatorsShouldBeConfigured() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.fadd)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.fsub)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.fmul)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.fdiv)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void supportForAllArithmeticDoubleOperatorsShouldBeConfigured() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.dadd)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.dsub)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.dmul)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.ddiv)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void supportForAllArithmeticLongOperatorsShouldBeConfigured() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.ladd)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.lsub)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.lmul)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.ldiv)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void iaddShouldPushAdditionOfStackedOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.iadd);

        expect(stack).toBe(iterableOf(add(constant(1), constant(2), int.class)));
    }

    @Test
    public void isubShouldPushSubtractionOfOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.isub);

        expect(stack).toBe(iterableOf(sub(constant(1), constant(2), int.class)));
    }

    @Test
    public void imulShouldPushMultiplicationOfOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.imul);

        expect(stack).toBe(iterableOf(mul(constant(1), constant(2), int.class)));
    }

    @Test
    public void idivShouldPushDivisionOfOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.idiv);

        expect(stack).toBe(iterableOf(div(constant(1), constant(2), int.class)));
    }

    @Test
    public void faddShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fadd);

        expect(stack).toBe(iterableOf(add(constant(1f), constant(2f), float.class)));
    }

    @Test
    public void fsubShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fsub);

        expect(stack).toBe(iterableOf(sub(constant(1f), constant(2f), float.class)));
    }

    @Test
    public void fmulShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fmul);

        expect(stack).toBe(iterableOf(mul(constant(1f), constant(2f), float.class)));
    }

    @Test
    public void fdivShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fdiv);

        expect(stack).toBe(iterableOf(div(constant(1f), constant(2f), float.class)));
    }

    @Test
    public void daddShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.dadd);

        expect(stack).toBe(iterableOf(add(constant(1d), constant(2d), double.class)));
    }

    @Test
    public void dsubShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.dsub);

        expect(stack).toBe(iterableOf(sub(constant(1d), constant(2d), double.class)));
    }

    @Test
    public void dmulShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.dmul);

        expect(stack).toBe(iterableOf(mul(constant(1d), constant(2d), double.class)));
    }

    @Test
    public void ddivShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.ddiv);

        expect(stack).toBe(iterableOf(div(constant(1d), constant(2d), double.class)));
    }

    @Test
    public void laddShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.ladd);

        expect(stack).toBe(iterableOf(add(constant(1l), constant(2l), long.class)));
    }

    @Test
    public void lsubShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.lsub);

        expect(stack).toBe(iterableOf(sub(constant(1l), constant(2l), long.class)));
    }

    @Test
    public void lmulShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.lmul);

        expect(stack).toBe(iterableOf(mul(constant(1l), constant(2l), long.class)));
    }

    @Test
    public void ldivShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.ldiv);

        expect(stack).toBe(iterableOf(div(constant(1l), constant(2l), long.class)));
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode)
                .apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();
        delegation.configure(builder);
        return builder.build();
    }
}