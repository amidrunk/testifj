package org.testifj.lang.impl;

import org.testifj.lang.ByteCode;
import org.testifj.lang.DecompilerConfiguration;
import org.testifj.lang.DecompilerExtension;

public final class FieldDecompilationExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        configurationBuilder.extend(ByteCode.putfield, putfield());
    }

    private static DecompilerExtension putfield() {
        return (context, codeStream, byteCode) -> {
            return true;
        };
    }

}
