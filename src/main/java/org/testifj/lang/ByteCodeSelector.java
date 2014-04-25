package org.testifj.lang;

// TODO More like a "DecompilerHook" or "DecompilationStateSelector" or something
public interface ByteCodeSelector {

    int getByteCode();

    boolean select(DecompilationContext context, int byteCode);

    static ByteCodeSelector forByteCode(int byteCode) {
        assert (byteCode & ~0xFF) == 0 : "Byte code must be in range [0, 255]";

        return new ByteCodeSelector() {
            @Override
            public int getByteCode() {
                return byteCode;
            }

            @Override
            public boolean select(DecompilationContext context, int byteCode) {
                return getByteCode() == byteCode;
            }
        };
    }
}
