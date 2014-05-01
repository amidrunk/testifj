package org.testifj.lang;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.testifj.Expect.expect;

public class DecompilationProgressCallbackTest {

    @Test
    public void nullCallbackShouldIgnoreCall() {
        final DecompilationContext context = mock(DecompilationContext.class);

        expect(() -> DecompilationProgressCallback.NULL.onDecompilationProgressed(context)).not().toThrow();

        verifyZeroInteractions(context);
    }

}
