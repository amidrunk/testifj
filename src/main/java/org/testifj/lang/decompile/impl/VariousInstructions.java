package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.DecompilerConfigurationBuilder;
import org.testifj.lang.decompile.DecompilerDelegate;
import org.testifj.lang.decompile.DecompilerDelegation;

public final class VariousInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.nop).then(nop());
    }

    public static DecompilerDelegate nop() {
        return DecompilerDelegate.NOP;
    }
}
