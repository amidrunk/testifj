package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerEnhancement;
import org.testifj.lang.decompile.impl.DecompilerEnhancementLink;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;

public class DecompilerEnhancementLinkTest {

    private final DecompilerEnhancement firstEnhancement = mock(DecompilerEnhancement.class);

    private final DecompilerEnhancement lastEnhancement = mock(DecompilerEnhancement.class);

    @Test
    public void constructorShouldNotAcceptAnyNullEnhancer() {
        expect(() -> new DecompilerEnhancementLink(null, lastEnhancement)).toThrow(AssertionError.class);
        expect(() -> new DecompilerEnhancementLink(firstEnhancement, null)).toThrow(AssertionError.class);
    }

    @Test
    public void firstAndLastEnhancementShouldAlwaysBeCalledOnEnhance() throws IOException {
        final DecompilerEnhancementLink link = new DecompilerEnhancementLink(firstEnhancement, lastEnhancement);
        final DecompilationContext context = mock(DecompilationContext.class);
        final CodeStream codeStream = mock(CodeStream.class);

        link.enhance(context, codeStream, ByteCode.nop);

        verify(firstEnhancement).enhance(eq(context), eq(codeStream), eq(ByteCode.nop));
        verify(lastEnhancement).enhance(eq(context), eq(codeStream), eq(ByteCode.nop));
    }
}
