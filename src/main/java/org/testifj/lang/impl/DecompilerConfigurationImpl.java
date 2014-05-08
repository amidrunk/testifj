package org.testifj.lang.impl;

import org.testifj.lang.*;
import org.testifj.util.Priority;

import java.lang.reflect.Array;
import java.util.Arrays;

public final class DecompilerConfigurationImpl implements DecompilerConfiguration {

    private final DecompilerDelegateAdapter[][] decompilerExtensions;

    private final DecompilerEnhancement[] decompilerEnhancements;

    private DecompilerConfigurationImpl(DecompilerDelegateAdapter[][] decompilerExtensions, DecompilerEnhancement[] decompilerEnhancements) {
        this.decompilerExtensions = decompilerExtensions;
        this.decompilerEnhancements = decompilerEnhancements;
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
    public DecompilerEnhancement getDecompilerEnhancement(DecompilationContext context, int byteCode) {
        assert context != null : "Decompilation context can't be null";
        assert validByteCode(byteCode) : "Byte code must be in range [0, 255]";

        return decompilerEnhancements[byteCode];
    }

    private static boolean validByteCode(int byteCode) {
        return (byteCode & ~0xFF) == 0;
    }

    @SuppressWarnings("unchecked")
    public static class Builder implements DecompilerConfiguration.Builder {

        private final DecompilerDelegateAdapter<DecompilerExtension>[][] decompilerExtensions = new DecompilerDelegateAdapter[256][];

        private final DecompilerEnhancement[] decompilerEnhancements = new DecompilerEnhancement[256];

        public DecompilerConfiguration build() {
            return new DecompilerConfigurationImpl(decompilerExtensions, decompilerEnhancements);
        }

        @Override
        public DecompilerConfiguration.Builder enhance(int byteCode, DecompilerEnhancement enhancement) {
            assert validByteCode(byteCode) : "Byte code must be in range [0, 255]";
            assert enhancement != null : "Enhancement can't be null";

            final DecompilerEnhancement existingEnhancement = decompilerEnhancements[byteCode];

            if (existingEnhancement != null) {
                decompilerEnhancements[byteCode] = new DecompilerEnhancementLink(existingEnhancement, enhancement);
            } else {
                decompilerEnhancements[byteCode] = enhancement;
            }

            return this;
        }

        @Override
        public ExtendContinuation on(int startByteCode, int endByteCode) {
            assert endByteCode > startByteCode : "End byte code must be greater than start byte code";
            assert ByteCode.isValid(startByteCode) : "Start byte code is not valid";
            assert ByteCode.isValid(endByteCode) : "End byte code is not valid";

            final int[] byteCodes = new int[endByteCode - startByteCode + 1];

            for (int i = startByteCode; i <= endByteCode; i++) {
                byteCodes[i - startByteCode] = i;
            }

            return new DecompilerExtensionBuilder(byteCodes);
        }

        @Override
        public ExtendContinuation on(int byteCode) {
            assert ByteCode.isValid(byteCode) : "Byte code is not valid";
            return new DecompilerExtensionBuilder(new int[]{byteCode});
        }

        private class DecompilerExtensionBuilder implements ExtendContinuation {

            private final int[] byteCodes;

            private Priority priority = Priority.DEFAULT;

            private DecompilationStateSelector decompilationStateSelector = DecompilationStateSelector.ALL;

            private DecompilerExtensionBuilder(int[] byteCodes) {
                this.byteCodes = byteCodes;
            }

            @Override
            public WithPriorityContinuation withPriority(Priority priority) {
                this.priority = priority;
                return this;
            }

            @Override
            public WhenContinuation when(DecompilationStateSelector selector) {
                this.decompilationStateSelector = selector;
                return this;
            }

            @Override
            public DecompilerConfiguration.Builder then(DecompilerExtension extension) {
                for (int byteCode : byteCodes) {
                    final DecompilerDelegateAdapter<DecompilerExtension>[] existingExtensions = decompilerExtensions[byteCode];
                    final DecompilerDelegateAdapter<DecompilerExtension> newAdapter = new DecompilerDelegateAdapter<>(byteCode, priority, decompilationStateSelector, extension);

                    if (existingExtensions == null) {
                        decompilerExtensions[byteCode] = new DecompilerDelegateAdapter[]{newAdapter};
                    } else {
                        final DecompilerDelegateAdapter<DecompilerExtension>[] newExtensions = Arrays.copyOf(existingExtensions, existingExtensions.length + 1);

                        for (int i = 0; i < newExtensions.length; i++) {
                            if (newExtensions[i] == null || priority.ordinal() > newExtensions[i].getPriority().ordinal()) {
                                System.arraycopy(newExtensions, i, newExtensions, i + 1, newExtensions.length - 1 - i);
                                newExtensions[i] = newAdapter;
                            }
                        }

                        decompilerExtensions[byteCode] = newExtensions;
                    }
                }

                return Builder.this;
            }
        }

    }

}
