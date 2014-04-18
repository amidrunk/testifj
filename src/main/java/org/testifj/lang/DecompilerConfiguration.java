package org.testifj.lang;

public interface DecompilerConfiguration {

    DecompilerExtension getDecompilerExtension(DecompilationContext context, int byteCode);

    public interface Builder {

        Builder extend(int startByteCode, int endByteCode, DecompilerExtension extension);

        Builder extend(int byteCode, DecompilerExtension extension);

        DecompilerConfiguration build();

    }

}
