package org.testifj.lang.impl;

import com.sun.tools.classfile.Annotation;
import org.junit.Test;
import org.testifj.lang.*;
import sun.jvm.hotspot.interpreter.Bytecode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class DecompilerConfigurationImplTest {

    private final DecompilerConfigurationImpl.Builder builder = new DecompilerConfigurationImpl.Builder();
    private final DecompilerConfiguration emptyConfiguration = builder.build();
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
        final DecompilerConfiguration configuration = builder
                .extend(ByteCode.nop, extension1)
                .build();

        final DecompilerExtension extension = configuration.getDecompilerExtension(decompilationContext, ByteCode.nop);

        extension.decompile(decompilationContext, codeStream, ByteCode.nop);

        verify(extension1).decompile(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
    }

    @Test
    public void extendShouldNotAcceptInvalidArguments() {
        final DecompilerConfigurationImpl.Builder builder = this.builder;

        expect(() -> builder.extend(-1, extension1)).toThrow(AssertionError.class);
        expect(() -> builder.extend(257, extension1)).toThrow(AssertionError.class);
        expect(() -> builder.extend(100, null)).toThrow(AssertionError.class);
    }

    @Test
    public void multipleExtensionsCanExistsForTheSameByteCode() throws IOException {
        final DecompilerConfiguration configuration = builder
                .extend(ByteCode.nop, extension1)
                .extend(ByteCode.nop, extension2)
                .build();

        configuration.getDecompilerExtension(decompilationContext, ByteCode.nop).decompile(decompilationContext, codeStream, ByteCode.nop);

        verify(extension1).decompile(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
        verify(extension2).decompile(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
    }

    @Test
    public void extendByteCodeRangeShouldNotAcceptInvalidRange() {
        expect(() -> builder.extend(1, 0, extension1)).toThrow(AssertionError.class);
        expect(() -> builder.extend(1, 1, extension1)).toThrow(AssertionError.class);
    }

    @Test
    public void byteCodeRangeCanBeExtended() throws IOException {
        final DecompilerExtension extension = mock(DecompilerExtension.class);
        final DecompilerConfiguration configuration = builder
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

    @Test
    public void enhanceInBuilderShouldNotAcceptInvalidArguments() {
        expect(() -> builder.enhance(123, null)).toThrow(AssertionError.class);
        expect(() -> builder.enhance(-1, mock(DecompilerEnhancement.class))).toThrow(AssertionError.class);
        expect(() -> builder.enhance(257, mock(DecompilerEnhancement.class))).toThrow(AssertionError.class);
    }

    @Test
    public void builderCanConfigureByteCodeEnhancement() {
        final DecompilerEnhancement enhancement = mock(DecompilerEnhancement.class);
        final DecompilerConfiguration.Builder builder = this.builder.enhance(ByteCode.new_, enhancement);

        given(builder.build()).then(it -> {
            expect(it.getDecompilerEnhancement(mock(DecompilationContext.class), ByteCode.new_)).toBe(enhancement);
        });
    }

    @Test
    public void configurationShouldReturnNullEnhancementForByteCodeIfNoEnhancementIsConfigured() {
        given(new DecompilerConfigurationImpl.Builder().build()).then(it -> {
            expect(it.getDecompilerEnhancement(decompilationContext, ByteCode.nop)).toBe(equalTo(null));
        });
    }

    @Test
    public void multipleEnhancementsCanBeConfiguredForTheSameByteCode() {
        final DecompilerEnhancement enhancement1 = mock(DecompilerEnhancement.class);
        final DecompilerEnhancement enhancement2 = mock(DecompilerEnhancement.class);

        given(new DecompilerConfigurationImpl.Builder()).then(builder -> {
            builder.enhance(ByteCode.nop, enhancement1);
            builder.enhance(ByteCode.nop, enhancement2);

            given(builder.build()).then(configuration -> {
                final DecompilerEnhancement enhancement = configuration.getDecompilerEnhancement(decompilationContext, ByteCode.nop);

                enhancement.enhance(decompilationContext, codeStream, ByteCode.nop);

                verify(enhancement1).enhance(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
                verify(enhancement2).enhance(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
            });
        });
    }

    @Test
    public void getDecompilerEnhancementShouldNotAcceptInvalidParameters() {
        given(new DecompilerConfigurationImpl.Builder().build()).then(it -> {
            expect(() -> it.getDecompilerEnhancement(null, 0)).toThrow(AssertionError.class);
            expect(() -> it.getDecompilerEnhancement(decompilationContext, -1)).toThrow(AssertionError.class);
            expect(() -> it.getDecompilerEnhancement(decompilationContext, 257)).toThrow(AssertionError.class);
        });
    }

}
