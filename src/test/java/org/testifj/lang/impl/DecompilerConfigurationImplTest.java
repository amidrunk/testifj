package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class DecompilerConfigurationImplTest {

    private final DecompilerConfiguration emptyConfiguration = new DecompilerConfigurationImpl.Builder().build();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final DecompilerExtension extension1 = mock(DecompilerExtension.class);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final DecompilerExtension extension2 = mock(DecompilerExtension.class);

    @Test
    public void getDecompilerExtensionShouldFailForInvalidArguments() {
        expect(() -> emptyConfiguration.getDecompilerExtension(null, 1)).toThrow(AssertionError.class);
        expect(() -> emptyConfiguration.getDecompilerExtension(decompilationContext, -1)).toThrow(AssertionError.class);
        expect(() -> emptyConfiguration.getDecompilerExtension(decompilationContext, 257)).toThrow(AssertionError.class);
    }

    @Test
    public void emptyConfigurationShouldReturnNullExtensionForAllByteCodes() {
        for (int i = 0; i < 256; i++) {
            expect(emptyConfiguration.getDecompilerExtension(decompilationContext, i)).toBe(equalTo(null));
        }
    }

    @Test
    public void configurationCanHaveSingleExtensionForByteCode() throws IOException {
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .extend(ByteCode.nop, extension1)
                .build();

        final DecompilerExtension extension = configuration.getDecompilerExtension(decompilationContext, ByteCode.nop);

        extension.decompile(decompilationContext, codeStream, ByteCode.nop);

        verify(extension1).decompile(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
    }

    @Test
    public void extendShouldNotAcceptInvalidArguments() {
        final DecompilerConfigurationImpl.Builder builder = new DecompilerConfigurationImpl.Builder();

        expect(() -> builder.extend(-1, extension1)).toThrow(AssertionError.class);
        expect(() -> builder.extend(257, extension1)).toThrow(AssertionError.class);
        expect(() -> builder.extend(100, null)).toThrow(AssertionError.class);
    }

    @Test
    public void multipleExtensionsCanExistsForTheSameByteCode() throws IOException {
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .extend(ByteCode.nop, extension1)
                .extend(ByteCode.nop, extension2)
                .build();

        configuration.getDecompilerExtension(decompilationContext, ByteCode.nop).decompile(decompilationContext, codeStream, ByteCode.nop);

        verify(extension1).decompile(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
        verify(extension2).decompile(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
    }

    @Test
    public void extendByteCodeRangeShouldNotAcceptInvalidRange() {
        expect(() -> new DecompilerConfigurationImpl.Builder().extend(1, 0, extension1)).toThrow(AssertionError.class);
        expect(() -> new DecompilerConfigurationImpl.Builder().extend(1, 1, extension1)).toThrow(AssertionError.class);
    }

    @Test
    public void byteCodeRangeCanBeExtended() throws IOException {
        final DecompilerExtension extension = mock(DecompilerExtension.class);
        final DecompilerConfiguration configuration = new DecompilerConfigurationImpl.Builder()
                .extend(ByteCode.iconst_0, ByteCode.iconst_5, extension)
                .build();

        final List<Integer> byteCodes = Arrays.asList(
                ByteCode.iconst_0,
                ByteCode.iconst_1,
                ByteCode.iconst_2,
                ByteCode.iconst_3,
                ByteCode.iconst_4,
                ByteCode.iconst_5);

        for (Integer byteCode : byteCodes) {
            final DecompilerExtension actualExtension = configuration.getDecompilerExtension(decompilationContext, byteCode);

            expect(actualExtension).toBe(extension);
        }
    }

}
