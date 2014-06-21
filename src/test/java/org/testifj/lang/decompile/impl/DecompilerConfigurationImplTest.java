package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.*;
import org.testifj.util.Priority;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.IteratorThatIs.emptyIterator;
import static org.testifj.matchers.core.IteratorThatIs.iteratorOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class DecompilerConfigurationImplTest {

    private final DecompilerConfigurationImpl.Builder builder = new DecompilerConfigurationImpl.Builder();
    private final DecompilerConfiguration emptyConfiguration = builder.build();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final DecompilerDelegate extension1 = mock(DecompilerDelegate.class, "extension1");
    private final CodeStream codeStream = mock(CodeStream.class);
    private final DecompilerDelegate extension2 = mock(DecompilerDelegate.class, "extension2");
    private final DecompilerDelegate enhancement1 = mock(DecompilerDelegate.class, "enhancement1");
    private final DecompilerDelegate enhancement2 = mock(DecompilerDelegate.class, "enhancement2");

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
                .on(ByteCode.nop).then(extension1)
                .build();

        final DecompilerDelegate extension = configuration.getDecompilerExtension(decompilationContext, ByteCode.nop);

        extension.apply(decompilationContext, codeStream, ByteCode.nop);

        verify(extension1).apply(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
    }

    @Test
    public void onShouldNotAcceptInvalidArguments() {
        final DecompilerConfigurationImpl.Builder builder = this.builder;

        expect(() -> builder.on(-1)).toThrow(AssertionError.class);
        expect(() -> builder.on(257)).toThrow(AssertionError.class);
        expect(() -> builder.on(100).then(null)).toThrow(AssertionError.class);
    }

    @Test
    public void multipleExtensionsCanExistsForTheSameByteCode() throws IOException {
        final DecompilationStateSelector selector1 = mock(DecompilationStateSelector.class);
        final DecompilationStateSelector selector2 = mock(DecompilationStateSelector.class);
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.nop).when(selector1).then(extension1)
                .on(ByteCode.nop).when(selector2).then(extension2)
                .build();

        when(selector1.select(eq(decompilationContext), eq(ByteCode.nop))).thenReturn(false);
        when(selector2.select(eq(decompilationContext), eq(ByteCode.nop))).thenReturn(true);

        expect(configuration.getDecompilerExtension(decompilationContext, ByteCode.nop)).toBe(extension2);

        verify(selector1).select(eq(decompilationContext), eq(ByteCode.nop));
        verify(selector2).select(eq(decompilationContext), eq(ByteCode.nop));
    }

    @Test
    public void extendByteCodeRangeShouldNotAcceptInvalidRange() {
        expect(() -> builder.on(1, 0)).toThrow(AssertionError.class);
        expect(() -> builder.on(1, 1)).toThrow(AssertionError.class);
    }

    @Test
    public void byteCodeRangeCanBeExtended() throws IOException {
        final DecompilerDelegate extension = mock(DecompilerDelegate.class);
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.iconst_0, ByteCode.iconst_5).then(extension)
                .build();

        final List<Integer> byteCodes = Arrays.asList(
                ByteCode.iconst_0,
                ByteCode.iconst_1,
                ByteCode.iconst_2,
                ByteCode.iconst_3,
                ByteCode.iconst_4,
                ByteCode.iconst_5);

        for (Integer byteCode : byteCodes) {
            final DecompilerDelegate actualExtension = configuration.getDecompilerExtension(decompilationContext, byteCode);

            expect(actualExtension).toBe(extension);
        }
    }

    @Test
    public void decompilerExtensionWithoutPriorityAndPredicateAndBeConfigured() {
        given(builder.on(ByteCode.nop).then(extension1).build()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.nop)).toBe(extension1);
        });
    }

    @Test
    public void decompilerExtensionWithPriorityAndNoPredicateCanBeConfigured() {
        given(builder.on(ByteCode.nop).withPriority(Priority.HIGH).then(extension1).build()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.nop)).toBe(extension1);
        });
    }

    @Test
    public void decompilerExtensionWithPriorityAndPredicateCanBeConfigured() {
        final DecompilationStateSelector selector = mock(DecompilationStateSelector.class);

        given(builder.on(ByteCode.nop).withPriority(Priority.HIGH).when(selector).then(extension1).build()).then(it -> {
            when(selector.select(eq(decompilationContext), eq(ByteCode.nop))).thenReturn(true);
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.nop)).toBe(extension1);
        });
    }

    @Test
    public void multipleMatchingDecompilerExtensionsWithDifferentPrioritiesCanBeConfigured() {
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.nop).withPriority(Priority.HIGH).then(extension1)
                .on(ByteCode.nop).withPriority(Priority.HIGHER).then(extension2)
                .build();

        expect(configuration.getDecompilerExtension(decompilationContext, ByteCode.nop)).toBe(extension2);
    }

    @Test
    public void onByteCodesShouldNotAcceptInvalidArguments() {
        expect(() -> builder.on()).toThrow(AssertionError.class);
        expect(() -> builder.on((int[]) null)).toThrow(AssertionError.class);
        expect(() -> builder.on(1234)).toThrow(AssertionError.class);
    }

    @Test
    public void onByteCodesShouldSetupExtensionsForByteCodes() {
        final Range range = Range.from(1).to(3);

        given(builder.on(range.all()).then(extension1).build()).then(it -> {
            range.each(byteCode -> expect(it.getDecompilerExtension(decompilationContext, byteCode)).not().toBe(equalTo(null)));
        });
    }

    @Test
    public void getAdvisoryDecompilerEnhancementsShouldNotAcceptInvalidArguments() {
        final DecompilerConfiguration configuration = builder.build();

        expect(() -> configuration.getAdvisoryDecompilerEnhancements(null, 1)).toThrow(AssertionError.class);
    }

    @Test
    public void getAdvisoryDecompilerEnhancementsShouldReturnEmptyIteratorIfNoMatchingEnhancementExists() {
        given(builder.build()).then(configuration -> {
            expect(configuration.getAdvisoryDecompilerEnhancements(decompilationContext, 1)).toBe(emptyIterator());
        });
    }

    @Test
    public void getAdvisoryDecompilerEnhancementsShouldReturnConfiguredEnhancementsInPriorityOrder() {
        final DecompilerConfiguration configuration = builder
                .before(ByteCode.nop).withPriority(Priority.DEFAULT).then(enhancement1)
                .before(ByteCode.nop).withPriority(Priority.HIGH).then(enhancement2)
                .build();

        final Iterator<DecompilerDelegate> iterator = configuration.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.nop);

        expect(iterator).toBe(iteratorOf(enhancement2, enhancement1));
    }

    @Test
    public void getCorrectionalDecompilerEnhancementsShouldNotAcceptInvalidArguments() {
        final DecompilerConfiguration configuration = builder.build();

        expect(() -> configuration.getCorrectionalDecompilerEnhancements(null, 0)).toThrow(AssertionError.class);
        expect(() -> configuration.getCorrectionalDecompilerEnhancements(decompilationContext, -1)).toThrow(AssertionError.class);
    }

    @Test
    public void getCorrectionalDecompilerEnhancementsShouldReturnEmptyIteratorIfNoMatchesExist() {
        expect(builder.build().getCorrectionalDecompilerEnhancements(decompilationContext, 1)).toBe(emptyIterator());
    }

    @Test
    public void getCorrectionalDecompilerEnhancementsShouldReturnMatchingCorrectors() {
        final DecompilerConfiguration configuration = builder
                .after(ByteCode.nop).withPriority(Priority.DEFAULT).then(enhancement1)
                .after(ByteCode.nop).withPriority(Priority.HIGH).then(enhancement2)
                .build();

        final Iterator<DecompilerDelegate> iterator = configuration.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.nop);

        expect(iterator).toBe(iteratorOf(enhancement2, enhancement1));
    }

    @Test
    public void mergeShouldNotAcceptNullArg() {
        expect(() -> builder.build().merge(null)).toThrow(AssertionError.class);
    }

    @Test
    public void mergeShouldNotAcceptIncorrectType() {
        expect(() -> builder.build().merge(mock(DecompilerConfiguration.class))).toThrow(AssertionError.class);
    }

    @Test
    public void mergeShouldCreateUnionOfNonIntersectingExtensions() {
        final DecompilerConfiguration configuration1 = new DecompilerConfigurationImpl.Builder()
                .on(ByteCode.iadd).then(extension1)
                .build();

        final DecompilerConfiguration configuration2 = new DecompilerConfigurationImpl.Builder()
                .on(ByteCode.isub).then(extension2)
                .build();

        final DecompilerConfiguration mergedConfiguration = configuration1.merge(configuration2);

        expect(mergedConfiguration.getDecompilerExtension(decompilationContext, ByteCode.iadd)).toBe(extension1);
        expect(mergedConfiguration.getDecompilerExtension(decompilationContext, ByteCode.isub)).toBe(extension2);
    }

    @Test
    public void mergeShouldOrganizeEqualExtensionsInPriorityOrder() {
        final DecompilerConfiguration configuration1 = new DecompilerConfigurationImpl.Builder()
                .on(ByteCode.iadd).then(extension1)
                .build();

        final DecompilerConfiguration configuration2 = new DecompilerConfigurationImpl.Builder()
                .on(ByteCode.iadd).then(extension2)
                .build();

        given(configuration1.merge(configuration2)).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iadd)).toBe(extension1);
        });

        given(configuration2.merge(configuration1)).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iadd)).toBe(extension2);
        });
    }

    @Test
    public void mergeShouldMergeAdvisoryEnhancements() {
        final DecompilerConfiguration configuration1 = new DecompilerConfigurationImpl.Builder()
                .before(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = new DecompilerConfigurationImpl.Builder()
                .before(ByteCode.isub).then(enhancement2)
                .build();

        given(configuration1.merge(configuration2)).then(it -> {
            expect(it.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.iadd)).toBe(iteratorOf(enhancement1));
            expect(it.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.isub)).toBe(iteratorOf(enhancement2));
        });
    }

    @Test
    public void mergeShouldOrganizeOverlappingAdvisoryEnhancementsInPriorityOrder() {
        final DecompilerConfiguration configuration1 = new DecompilerConfigurationImpl.Builder()
                .before(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = new DecompilerConfigurationImpl.Builder()
                .before(ByteCode.iadd).then(enhancement2)
                .build();

        given(configuration1.merge(configuration2)).then(it -> {
            expect(it.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.iadd)).toBe(iteratorOf(enhancement1, enhancement2));
        });

        given(configuration2.merge(configuration1)).then(it -> {
            expect(it.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.iadd)).toBe(iteratorOf(enhancement2, enhancement1));
        });
    }

    @Test
    public void mergeShouldMergeCorrectionalEnhancements() {
        final DecompilerConfiguration configuration1 = new DecompilerConfigurationImpl.Builder()
                .after(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = new DecompilerConfigurationImpl.Builder()
                .after(ByteCode.isub).then(enhancement2)
                .build();

        given(configuration1.merge(configuration2)).then(it -> {
            expect(it.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iadd)).toBe(iteratorOf(enhancement1));
            expect(it.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.isub)).toBe(iteratorOf(enhancement2));
        });
    }

    @Test
    public void mergeShouldOrganizeOverlappingCorrectionalEnhancementsInPriorityOrder() {
        final DecompilerConfiguration configuration1 = new DecompilerConfigurationImpl.Builder()
                .after(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = new DecompilerConfigurationImpl.Builder()
                .after(ByteCode.iadd).then(enhancement2)
                .build();

        given(configuration1.merge(configuration2)).then(it -> {
            expect(it.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iadd)).toBe(iteratorOf(enhancement1, enhancement2));
        });

        given(configuration2.merge(configuration1)).then(it -> {
            expect(it.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iadd)).toBe(iteratorOf(enhancement2, enhancement1));
        });
    }

}
