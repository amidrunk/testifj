package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.CodeStreamTestUtils;
import org.testifj.lang.TypeResolver;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;
import org.testifj.lang.model.impl.IncrementImpl;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;
import org.testifj.matchers.core.IteratorThatIs;

import java.io.IOException;
import java.util.Iterator;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.*;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class UnaryOperationsTest {

    private final UnaryOperations unaryOperations = new UnaryOperations();
    private final Method method = mock(Method.class);
    private final DecompilationContext decompilationContext = new DecompilationContextImpl(mock(Decompiler.class), method, mock(ProgramCounter.class), mock(LineNumberCounter.class), mock(TypeResolver.class), 0);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final LocalVariable localVariable = mock(LocalVariable.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> unaryOperations.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForUnaryOperators() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.iinc)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void iincShouldIncreaseValueOfVariable() throws Exception {
        when(method.getLocalVariableForIndex(eq(1))).thenReturn(localVariable);
        when(localVariable.getName()).thenReturn("foo");
        when(localVariable.getType()).thenReturn(int.class);

        execute(ByteCode.iinc, 1, 2);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(
                new IncrementImpl(new LocalVariableReferenceImpl("foo", int.class, 1), AST.constant(2), int.class, Affix.UNDEFINED)
        ));
    }

    @Test
    public void postfixIncrementShouldBeCorrectedAfterIncrement() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(local, AST.constant(1), int.class, Affix.UNDEFINED);

        decompilationContext.push(local);
        decompilationContext.push(originalIncrement);

        configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iinc)
                .next()
                .apply(decompilationContext, codeStream, ByteCode.iinc);

        expect(decompilationContext.getStackedExpressions())
                .toBe(collectionOf(new IncrementImpl(local, AST.constant(1), int.class, Affix.POSTFIX)));
    }

    @Test
    public void prefixIncrementShouldBeCorrectedAfterIncrementAndLoad() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(local, AST.constant(1), int.class, Affix.UNDEFINED);

        decompilationContext.push(originalIncrement);
        decompilationContext.push(local);

        configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iload)
                .next()
                .apply(decompilationContext, codeStream, ByteCode.iinc);

        expect(decompilationContext.getStackedExpressions())
                .toBe(collectionOf(new IncrementImpl(local, AST.constant(1), int.class,  Affix.PREFIX)));
    }

    @Test
    public void correctionalPrefixIncrementEnhancementShouldNotApplyIfLoadIsNotPrecededByIncrementOfVariable() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(AST.local("bar", int.class, 2), AST.constant(1), int.class, Affix.UNDEFINED);

        decompilationContext.push(originalIncrement);
        decompilationContext.push(local);

        expect(configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iload)).toBe(IteratorThatIs.emptyIterator());
        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(originalIncrement, local));
    }

    @Test
    public void correctionalPrefixIncrementEnhancementShouldIgnoreQualifiedIncrement() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(local, AST.constant(1), int.class, Affix.POSTFIX);

        decompilationContext.push(originalIncrement);
        decompilationContext.push(local);

        expect(configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iload)).toBe(IteratorThatIs.emptyIterator());
        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(originalIncrement, local));
    }

    @Test
    public void prefixByteIncrementShouldBeCorrectedAfterLoad() throws IOException {
        final LocalVariableReference local = local("myVar", byte.class, 1);

        decompilationContext.enlist(set(local).to(cast(sub(local, constant(1), int.class)).to(byte.class)));
        decompilationContext.push(local);

        after(ByteCode.iload_1);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(new IncrementImpl(local, constant(-1), byte.class, Affix.PREFIX)));
    }

    @Test
    public void postfixByteIncrementShouldBeCorrectedAfterLoad() throws Exception {
        final LocalVariableReference local = local("myVar", byte.class, 1);

        decompilationContext.push(local);
        decompilationContext.enlist(set(local).to(cast(add(local, constant(1), int.class)).to(byte.class)));

        after(ByteCode.istore_1);

        expect(decompilationContext.getStackedExpressions()).toBe(collectionOf(new IncrementImpl(local, constant(1), byte.class, Affix.POSTFIX)));
    }

    @Test
    public void prefixFloatDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", float.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1f), float.class)));
        decompilationContext.push(sub(local, constant(1f), float.class));

        after(ByteCode.fstore_0);

        expect(decompilationContext.getStack()).toBe(iterableOf(new IncrementImpl(local, constant(-1f), float.class, Affix.PREFIX)));
    }

    @Test
    public void postfixFloatDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", float.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1f), float.class)));
        decompilationContext.push(local);

        after(ByteCode.fstore_0);

        expect(decompilationContext.getStack()).toBe(iterableOf(new IncrementImpl(local, constant(-1f), float.class, Affix.POSTFIX)));
    }

    @Test
    public void prefixDoubleDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", double.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1d), double.class)));
        decompilationContext.push(sub(local, constant(1d), double.class));

        after(ByteCode.dstore_0);

        expect(decompilationContext.getStack()).toBe(iterableOf(new IncrementImpl(local, constant(-1d), double.class, Affix.PREFIX)));
    }

    @Test
    public void postfixDoubleDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", double.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1d), double.class)));
        decompilationContext.push(local);

        after(ByteCode.dstore_0);

        expect(decompilationContext.getStack()).toBe(iterableOf(new IncrementImpl(local, constant(-1d), double.class, Affix.POSTFIX)));
    }

    @Test
    public void prefixLongDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", long.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1l), long.class)));
        decompilationContext.push(sub(local, constant(1l), long.class));

        after(ByteCode.lstore_0);

        expect(decompilationContext.getStack()).toBe(iterableOf(new IncrementImpl(local, constant(-1l), long.class, Affix.PREFIX)));
    }

    @Test
    public void postfixLongDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", long.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1l), long.class)));
        decompilationContext.push(local);

        after(ByteCode.lstore_0);

        expect(decompilationContext.getStack()).toBe(iterableOf(new IncrementImpl(local, constant(-1l), long.class, Affix.POSTFIX)));
    }


    private void after(int byteCode) throws IOException {
        final Iterator<DecompilerDelegate> iterator = configuration().getCorrectionalDecompilerEnhancements(decompilationContext, byteCode);

        while (iterator.hasNext()) {
            iterator.next().apply(decompilationContext, codeStream, byteCode);
        }
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode).apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationImpl.Builder builder = new DecompilerConfigurationImpl.Builder();
        unaryOperations.configure(builder);
        return builder.build();
    }

}