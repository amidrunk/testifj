package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.ByteCode;
import org.testifj.lang.CodeStream;
import org.testifj.lang.DecompilationContext;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.ArrayStoreImpl;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ArrayDecompilerExtensionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);
    private final CodeStream code = mock(CodeStream.class);

    @Test
    public void objectArrayStoreShouldEnlistNewArrayStoreOnReferencedArray() throws IOException {
        final Expression value = mock(Expression.class, "value");
        final Expression index = mock(Expression.class, "index");
        final Expression array = mock(Expression.class, "array");

        when(context.pop()).thenReturn(value, index, array);

        ArrayDecompilerExtensions.aastore().decompile(context, code, ByteCode.aastore);

        verify(context).enlist(eq(new ArrayStoreImpl(array, index, value)));
    }

}
