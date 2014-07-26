package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfigurationBuilder;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class VariousInstructionsTest {

    private final VariousInstructions variousInstructions = new VariousInstructions();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> variousInstructions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForNopInstruction() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(decompilationContext, ByteCode.nop)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void nopInstructionShouldBeHandled() throws IOException {
        execute(ByteCode.nop);

        verifyZeroInteractions(decompilationContext);
        verifyNoMoreInteractions(codeStream);
    }

    private void execute(int instruction) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, instruction).apply(decompilationContext, codeStream, instruction);
    }

    private org.testifj.lang.decompile.DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();
        variousInstructions.configure(builder);
        return builder.build();
    }

}