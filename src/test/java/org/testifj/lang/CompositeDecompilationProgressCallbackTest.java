package org.testifj.lang;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.CompositeDecompilationProgressCallback;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilationProgressCallback;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class CompositeDecompilationProgressCallbackTest {

    private final DecompilationProgressCallback callback1 = mock(DecompilationProgressCallback.class);
    private final DecompilationProgressCallback callback2 = mock(DecompilationProgressCallback.class);
    private final CompositeDecompilationProgressCallback callback = new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{callback1, callback2});
    private final DecompilationContext context = mock(DecompilationContext.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CompositeDecompilationProgressCallback(null)).toThrow(AssertionError.class);
        expect(() -> new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{null})).toThrow(AssertionError.class);
    }

    @Test
    public void beforeInstructionShouldDelegateToTargetCallbacks() {
        callback.beforeInstruction(context);

        final InOrder inOrder = Mockito.inOrder(callback1, callback2);

        inOrder.verify(callback1).beforeInstruction(eq(context));
        inOrder.verify(callback2).beforeInstruction(eq(context));
    }

    @Test
    public void preparingInstructionShouldDelegateToTargetCallbacks() {
        callback.preparingInstruction(context, ByteCode.nop);

        final InOrder inOrder = Mockito.inOrder(callback1, callback2);

        inOrder.verify(callback1).preparingInstruction(eq(context), eq(ByteCode.nop));
        inOrder.verify(callback2).preparingInstruction(eq(context), eq(ByteCode.nop));
    }

    @Test
    public void afterInstructionShouldDelegateToTargetCallbacks() {
        callback.afterInstruction(context);

        final InOrder inOrder = Mockito.inOrder(callback1, callback2);

        inOrder.verify(callback1).afterInstruction(eq(context));
        inOrder.verify(callback2).afterInstruction(eq(context));
    }

}
