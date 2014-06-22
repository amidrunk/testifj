package org.testifj.lang.decompile;

import org.testifj.lang.decompile.impl.*;

public final class CoreDecompilerDelegation implements DecompilerDelegation {

    private final DecompilerDelegation[] delegations = new DecompilerDelegation[]{
            new VariableInstructions(),
            new ArrayInstructions(),
            new InstantiationInstructions(),
            new InvokeDynamicInstructions(),
            new MethodCallInstructions(),
            new FieldInstructions(),
            new CastInstructions(),
            new BinaryOperations(),
            new ConstantInstructions(),
            new StackInstructions(),
            new VariousInstructions(),
            new UnaryOperations(),
            new ControlFlowInstructions()
    };

    @Override
    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
        for (DecompilerDelegation delegation : delegations) {
            delegation.configure(configurationBuilder);
        }
    }

    public static DecompilerConfiguration configuration() {
        final DecompilerConfigurationImpl.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();
        new CoreDecompilerDelegation().configure(configurationBuilder);
        return configurationBuilder.build();
    }
}
