package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.CodeStream;
import org.testifj.lang.DecompilationContext;
import org.testifj.lang.DecompilerExtension;
import org.testifj.lang.impl.DecompilerExtensionList;
import org.testifj.matchers.core.CollectionThatIs;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

public class DecompilerExtensionListTest {

    private final DecompilerExtensionList empty = new DecompilerExtensionList();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Test
    public void emptyDecompilerExtensionListShouldHaveNoExtensions() {
        expect(empty.extensions()).toBe(CollectionThatIs.empty());
    }

    @Test
    public void extendShouldNotAcceptNullArg() {
        expect(() -> empty.extend(null)).toThrow(AssertionError.class);
    }

    @Test
    public void extendShouldReturnNewExtensionListWithProvidedExtension() {
        final DecompilerExtension extension = mock(DecompilerExtension.class);
        final DecompilerExtensionList newExtensionList = empty.extend(extension);

        expect(empty.extensions()).toBe(CollectionThatIs.empty());
        expect(newExtensionList.extensions().toArray()).toBe(new Object[]{extension});
    }

    @Test
    public void decompileShouldReturnFalseIfNoExtensionsExist() throws Exception {
        expect(empty.decompile(decompilationContext, codeStream, 1)).toBe(false);
    }

    @Test
    public void decompileShouldReturnFalseIfNoExtensionMatchers() throws Exception {
        final DecompilerExtension extension = mock(DecompilerExtension.class);
        final DecompilerExtensionList extended = empty.extend(extension);

        expect(extended.decompile(decompilationContext, codeStream, 1)).toBe(false);

        verify(extension).decompile(eq(decompilationContext), eq(codeStream), eq(1));
    }

    @Test
    public void decompileShouldAbortAtFirstMatchingExtensionAndReturnTrue() throws IOException {
        final DecompilerExtension extension1 = mock(DecompilerExtension.class);
        final DecompilerExtension extension2 = mock(DecompilerExtension.class);
        final DecompilerExtensionList extended = empty.extend(extension1).extend(extension2);

        when(extension1.decompile(eq(decompilationContext), eq(codeStream), eq(1))).thenReturn(true);

        expect(extended.decompile(decompilationContext, codeStream, 1)).toBe(true);

        verify(extension1).decompile(eq(decompilationContext), eq(codeStream), eq(1));
        verifyZeroInteractions(extension2);
    }

}
