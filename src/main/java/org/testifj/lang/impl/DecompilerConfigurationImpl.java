package org.testifj.lang.impl;

import org.testifj.lang.DecompilationContext;
import org.testifj.lang.DecompilerConfiguration;
import org.testifj.lang.DecompilerExtension;
import org.testifj.lang.DecompilerExtensionList;

public final class DecompilerConfigurationImpl implements DecompilerConfiguration {

    private final DecompilerExtension[] decompilerExtensions;

    private DecompilerConfigurationImpl(DecompilerExtension[] decompilerExtensions) {
        this.decompilerExtensions = decompilerExtensions;
    }

    @Override
    public DecompilerExtension getDecompilerExtension(DecompilationContext context, int byteCode) {
        assert context != null : "Decompilation context can't be null";
        assert (byteCode & ~0xFF) == 0 : "Byte code must be in range [0, 255]";

        return decompilerExtensions[byteCode];
    }

    public static class Builder implements DecompilerConfiguration.Builder {

        private final DecompilerExtension[] decompilerExtensions = new DecompilerExtension[256];

        public DecompilerConfiguration build() {
            return new DecompilerConfigurationImpl(decompilerExtensions);
        }

        public Builder extend(int startByteCode, int endByteCode, DecompilerExtension extension) {
            assert endByteCode > startByteCode : "End byte code must be greater than start byte code";

            for (int i = startByteCode; i <= endByteCode; i++) {
                extend(i, extension);
            }

            return this;
        }

        public Builder extend(int byteCode, DecompilerExtension extension) {
            assert (byteCode & ~0xFF) == 0 : "Byte code must be in range [0, 255]";
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
    }

}
