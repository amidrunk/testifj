package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.util.Iterators;
import org.testifj.util.Priority;

import java.util.Arrays;
import java.util.Iterator;

import static org.testifj.util.Iterators.collect;
import static org.testifj.util.Iterators.empty;
import static org.testifj.util.Iterators.filter;

public final class DecompilerConfigurationImpl implements DecompilerConfiguration {

    private final DecompilerDelegateAdapter<DecompilerExtension>[][] decompilerExtensions;

    private final DecompilerDelegateAdapter<DecompilerEnhancement>[][] advisoryDecompilerEnhancements;

    private final DecompilerDelegateAdapter<DecompilerEnhancement>[][] correctionalDecompilerEnhancements;

    private DecompilerConfigurationImpl(DecompilerDelegateAdapter<DecompilerExtension>[][] decompilerExtensions,
                                        DecompilerDelegateAdapter<DecompilerEnhancement>[][] advisoryDecompilerEnhancements,
                                        DecompilerDelegateAdapter<DecompilerEnhancement>[][] correctionalDecompilerEnhancements) {
        this.decompilerExtensions = decompilerExtensions;
        this.advisoryDecompilerEnhancements = advisoryDecompilerEnhancements;
        this.correctionalDecompilerEnhancements = correctionalDecompilerEnhancements;
    }

    @Override
    public DecompilerExtension getDecompilerExtension(DecompilationContext context, int byteCode) {
        assert context != null : "Decompilation context can't be null";
        assert validByteCode(byteCode) : "Byte code must be in range [0, 255]";

        final DecompilerDelegateAdapter[] candidates = decompilerExtensions[byteCode];

        if (candidates == null) {
            return null;
        }

        for (DecompilerDelegateAdapter adapter : candidates) {
            if (adapter.getDecompilationStateSelector().select(context, byteCode)) {
                return (DecompilerExtension) adapter.getDelegate();
            }
        }

        return null;
    }

    @Override
    public Iterator<DecompilerEnhancement> getAdvisoryDecompilerEnhancements(DecompilationContext context, int byteCode) {
        return selectEnhancements(advisoryDecompilerEnhancements, context, byteCode);
    }

    @Override
    public Iterator<DecompilerEnhancement> getCorrectionalDecompilerEnhancements(DecompilationContext context, int byteCode) {
        return selectEnhancements(correctionalDecompilerEnhancements, context, byteCode);
    }

    private Iterator<DecompilerEnhancement> selectEnhancements(DecompilerDelegateAdapter<DecompilerEnhancement>[][] source,
                                                               DecompilationContext context, int byteCode) {
        assert context != null : "Context can't be null";
        assert ByteCode.isValid(byteCode) : "Byte code is not valid";

        final DecompilerDelegateAdapter<DecompilerEnhancement>[] enhancements = source[byteCode];

        if (enhancements == null) {
            return empty();
        }

        return collect(filter(Iterators.of(enhancements),
                        adapter -> adapter.getDecompilationStateSelector().select(context, byteCode)),
                DecompilerDelegateAdapter::getDelegate);
    }

    private static boolean validByteCode(int byteCode) {
        return (byteCode & ~0xFF) == 0;
    }

    @SuppressWarnings("unchecked")
    public static class Builder implements DecompilerConfiguration.Builder {

        private final DecompilerDelegateAdapter<DecompilerExtension>[][] decompilerExtensions = new DecompilerDelegateAdapter[256][];

        private final DecompilerDelegateAdapter<DecompilerEnhancement>[][] advisoryDecompilerEnhancements = new DecompilerDelegateAdapter[256][];

        private final DecompilerDelegateAdapter<DecompilerEnhancement>[][] correctionalDecompilerEnhancements = new DecompilerDelegateAdapter[256][];

        public DecompilerConfiguration build() {
            return new DecompilerConfigurationImpl(
                    decompilerExtensions,
                    advisoryDecompilerEnhancements,
                    correctionalDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerEnhancement> before(int byteCode) {
            return new DecompilerExtensionBuilder<>(this, new int[]{byteCode}, advisoryDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerEnhancement> after(int byteCode) {
            return new DecompilerExtensionBuilder<>(this, new int[]{byteCode}, correctionalDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerExtension> on(int startByteCode, int endByteCode) {
            assert endByteCode > startByteCode : "End byte code must be greater than start byte code";
            assert ByteCode.isValid(startByteCode) : "Start byte code is not valid";
            assert ByteCode.isValid(endByteCode) : "End byte code is not valid";

            final int[] byteCodes = new int[endByteCode - startByteCode + 1];

            for (int i = startByteCode; i <= endByteCode; i++) {
                byteCodes[i - startByteCode] = i;
            }

            return new DecompilerExtensionBuilder(this, byteCodes, decompilerExtensions);
        }

        @Override
        public ExtendContinuation<DecompilerExtension> on(int byteCode) {
            assert ByteCode.isValid(byteCode) : "Byte code is not valid";
            return new DecompilerExtensionBuilder(this, new int[]{byteCode}, decompilerExtensions);
        }

        @Override
        public ExtendContinuation<DecompilerExtension> on(int... byteCodes) {
            assert byteCodes != null : "Byte codes can't be null";
            assert byteCodes.length > 0 : "Byte codes can't be empty";

            final int[] copy = new int[byteCodes.length];

            for (int i = 0; i < byteCodes.length; i++) {
                assert ByteCode.isValid(byteCodes[i]) : "Byte code is not valid: " + byteCodes[i];

                copy[i] = byteCodes[i];
            }

            return new DecompilerExtensionBuilder<>(this, copy, decompilerExtensions);
        }

        private static class DecompilerExtensionBuilder<T> implements ExtendContinuation<T> {

            private final Builder builder;

            private final int[] byteCodes;

            private final DecompilerDelegateAdapter<T>[][] targetArray;

            private Priority priority = Priority.DEFAULT;

            private DecompilationStateSelector decompilationStateSelector = DecompilationStateSelector.ALL;

            private DecompilerExtensionBuilder(Builder builder, int[] byteCodes, DecompilerDelegateAdapter<T>[][] targetArray) {
                this.builder = builder;
                this.byteCodes = byteCodes;
                this.targetArray = targetArray;
            }

            @Override
            public WithPriorityContinuation<T> withPriority(Priority priority) {
                this.priority = priority;
                return this;
            }

            @Override
            public WhenContinuation<T> when(DecompilationStateSelector selector) {
                this.decompilationStateSelector = selector;
                return this;
            }

            @Override
            public DecompilerConfiguration.Builder then(T extension) {
                for (int byteCode : byteCodes) {
                    final DecompilerDelegateAdapter<T>[] existingExtensions = targetArray[byteCode];
                    final DecompilerDelegateAdapter<T> newAdapter = new DecompilerDelegateAdapter<>(byteCode, priority, decompilationStateSelector, extension);

                    if (existingExtensions == null) {
                        targetArray[byteCode] = new DecompilerDelegateAdapter[]{newAdapter};
                    } else {
                        final DecompilerDelegateAdapter<T>[] newExtensions = Arrays.copyOf(
                                existingExtensions,
                                existingExtensions.length + 1,
                                DecompilerDelegateAdapter[].class);

                        for (int i = 0; i < newExtensions.length; i++) {
                            if (newExtensions[i] == null || priority.ordinal() > newExtensions[i].getPriority().ordinal()) {
                                System.arraycopy(newExtensions, i, newExtensions, i + 1, newExtensions.length - 1 - i);
                                newExtensions[i] = newAdapter;
                                break;
                            }
                        }

                        targetArray[byteCode] = newExtensions;
                    }
                }

                return builder;
            }
        }

    }

}
