package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerDelegate;
import org.testifj.lang.decompile.DecompilerDelegation;

public final class VariousInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfiguration.Builder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.nop).then(nop());
    }

    public static DecompilerDelegate nop() {
        return DecompilerDelegate.NOP;
    }
}
