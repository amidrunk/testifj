package org.testifj.lang;

public interface DecompilerConfiguration {

    DecompilerExtension getDecompilerExtension(DecompilationContext context, int byteCode);

    DecompilerEnhancement getDecompilerEnhancement(DecompilationContext context, int byteCode);

    public interface Builder {

        Builder extend(int startByteCode, int endByteCode, DecompilerExtension extension);

        Builder extend(int byteCode, DecompilerExtension extension);

        /**
         * Adds an enhancement to the specified byte code. An enhancement is executed after the actual executement
         * of the byte code and cannot override the default behaviour of a byte code. Typically, this is used to hook
         * execution of a byte code to transform the stack and/or statement list to map recognized patterns to Java
         * syntax. For example, the "new" operator is ASTed by hooking the "invokespecial" operator. If the stack
         * contains an allocation and the called method is an "&lt;init&gt;" method, the enhancement will transform
         * the stack and replace it with the higher order new operator.
         *
         * @param byteCode The byte code to hook into.
         * @param enhancement The enhancement that will be called when the byte code occurs.
         * @return The same instance will always be returned.
         */
        Builder enhance(int byteCode, DecompilerEnhancement enhancement);

        DecompilerConfiguration build();

    }

}
