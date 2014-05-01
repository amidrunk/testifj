package org.testifj.lang;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class CompositeDecompilationProgressCallbackTest {

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        expect(() -> new CompositeDecompilationProgressCallback(null)).toThrow(AssertionError.class);
        expect(() -> new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{null})).toThrow(AssertionError.class);
    }

    @Test
    public void onDecompilationProgressShouldDelegateToTargetCallbacks() {
        final DecompilationProgressCallback callback1 = mock(DecompilationProgressCallback.class);
        final DecompilationProgressCallback callback2 = mock(DecompilationProgressCallback.class);
        final CompositeDecompilationProgressCallback callback = new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{callback1, callback2});
        final DecompilationContext context = mock(DecompilationContext.class);

        callback.onDecompilationProgressed(context);

        final InOrder inOrder = Mockito.inOrder(callback1, callback2);

        inOrder.verify(callback1).onDecompilationProgressed(eq(context));
        inOrder.verify(callback2).onDecompilationProgressed(eq(context));
    }

}
