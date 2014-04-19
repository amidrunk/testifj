package org.testifj.lang.impl;

import org.testifj.lang.*;

public final class DecompilerConfigurationImpl implements DecompilerConfiguration {

    private final DecompilerExtension[] decompilerExtensions;

    private final DecompilerEnhancement[] decompilerEnhancements;

    private DecompilerConfigurationImpl(DecompilerExtension[] decompilerExtensions, DecompilerEnhancement[] decompilerEnhancements) {
        this.decompilerExtensions = decompilerExtensions;
        this.decompilerEnhancements = decompilerEnhancements;
    }

    @Override
    public DecompilerExtension getDecompilerExtension(DecompilationContext context, int byteCode) {
        assert context != null : "Decompilation context can't be null";
        assert validByteCode(byteCode) : "Byte code must be in range [0, 255]";

        return decompilerExtensions[byteCode];
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

    public static class Builder implements DecompilerConfiguration.Builder {

        private final DecompilerExtension[] decompilerExtensions = new DecompilerExtension[256];

        private final DecompilerEnhancement[] decompilerEnhancements = new DecompilerEnhancement[256];

        public DecompilerConfiguration build() {
            return new DecompilerConfigurationImpl(decompilerExtensions, decompilerEnhancements);
        }

        public Builder extend(int startByteCode, int endByteCode, DecompilerExtension extension) {
            assert endByteCode > startByteCode : "End byte code must be greater than start byte code";

            for (int i = startByteCode; i <= endByteCode; i++) {
                extend(i, extension);
            }

            return this;
        }

        public Builder extend(int byteCode, DecompilerExtension extension) {
            assert validByteCode(byteCode) : "Byte code must be in range [0, 255]";
            assert extension != null : "Extension can't be null";

            final DecompilerExtension existingExtension = decompilerExtensions[byteCode];

            if (existingExtension != null) {
                if (existingExtension instanceof DecompilerExtensionList) {
                    decompilerExtensions[byteCode] = ((DecompilerExtensionList) existingExtension).extend(extension);
                } else {
                    decompilerExtensions[byteCode] = new DecompilerExtensionList()
                            .extend(existingExtension)
                            .extend(extension);
                }
            } else {
                decompilerExtensions[byteCode] = extension;
            }

            return this;
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
    }

}
