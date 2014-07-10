package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.lang.CodeStreamTestUtils;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.ProgramCounter;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.LocalVariableReference;
import org.testifj.lang.model.impl.GotoImpl;
import org.testifj.lang.model.impl.ReturnImpl;
import org.testifj.util.SingleThreadedStack;
import org.testifj.util.Stack;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.model.AST.$return;
import static org.testifj.lang.model.AST.constant;
import static org.testifj.lang.model.AST.local;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ControlFlowInstructionsTest {

    private final ControlFlowInstructions controlFlowInstructions = new ControlFlowInstructions();

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    private final Stack<Expression> stack = new SingleThreadedStack<>();

    @Before
    public void setup() {
        when(decompilationContext.getStack()).thenReturn(stack);
    }

    @Test
    public void configureShouldNotAcceptNullConfiguration() {
        expect(() -> controlFlowInstructions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configurationShouldSupportReturnInstructions() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.return_)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.ireturn)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.lreturn)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.freturn)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.dreturn)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.areturn)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void returnShouldReduceStackAndEnlistReturn() throws IOException {
        execute(ByteCode.return_);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).reduceAll();
        inOrder.verify(decompilationContext).enlist(eq(new ReturnImpl()));
    }

    @Test
    public void ireturnShouldEnlistReturnOfInteger() throws IOException {
        stack.push(constant(1));

        execute(ByteCode.ireturn);

        verify(decompilationContext).enlist(eq($return(constant(1))));
    }

    @Test
    public void lreturnShouldEnlistReturnOfLong() throws IOException {
        stack.push(constant(1L));

        execute(ByteCode.lreturn);

        verify(decompilationContext).enlist(eq($return(constant(1L))));
    }

    @Test
    public void freturnShouldEnlistReturnOfLong() throws IOException {
        stack.push(constant(1f));

        execute(ByteCode.freturn);

        verify(decompilationContext).enlist(eq($return(constant(1f))));
    }

    @Test
    public void dreturnShouldEnlistReturnOfLong() throws IOException {
        stack.push(constant(1d));

        execute(ByteCode.dreturn);

        verify(decompilationContext).enlist(eq($return(constant(1d))));
    }

    @Test
    public void ireturnShouldFailForInvalidReturnType() throws IOException {
        stack.push(constant(1d));

        expect(() -> execute(ByteCode.ireturn)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void areturnShouldEnlistReturnOfLong() throws IOException {
        final LocalVariableReference local = local("foo", String.class, 1);

        stack.push(local);

        execute(ByteCode.areturn);

        verify(decompilationContext).enlist(eq($return(local)));
    }

    @Test
    public void areturnShouldFailForInvalidReturnType() {
        stack.push(constant(1));

        expect(() -> execute(ByteCode.areturn)).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void gotoShouldEnlistGotoElement() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);

        when(pc.get()).thenReturn(100);
        when(decompilationContext.getProgramCounter()).thenReturn(pc);

        execute(ByteCode.goto_, 0, 10);

        verify(decompilationContext).enlist(eq(new GotoImpl(110)));
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode)
                .apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationImpl.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();
        controlFlowInstructions.configure(configurationBuilder);
        return configurationBuilder.build();
    }

}