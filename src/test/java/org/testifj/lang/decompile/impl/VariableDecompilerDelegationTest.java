package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.LocalVariableTable;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.LocalVariableImpl;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerDelegate;
import org.testifj.lang.model.AST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Describe.describe;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class VariableDecompilerDelegationTest {

    private final DecompilationContext context = mock(DecompilationContext.class);
    private final Method method = mock(Method.class);
    private final ClassFile classFile = mock(ClassFile.class);
    private final LocalVariableTable localVariableTable = mock(LocalVariableTable.class);

    private DecompilerConfiguration configuration = null;

    @Before
    public void setup() {
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        new VariableDecompilerDelegation().configure(configurationBuilder);

        configuration = configurationBuilder.build();

        when(context.getMethod()).thenReturn(method);
        when(context.getProgramCounter()).thenReturn(new ProgramCounterImpl());
        when(method.getClassFile()).thenReturn(classFile);
        when(method.getLocalVariableTable()).thenReturn(Optional.of(localVariableTable));
    }

    @Test
    public void configureShouldNotAcceptInvalidArgument() {
        expect(() -> new VariableDecompilerDelegation().configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForAllVariableOperations() {
        Range.from(ByteCode.iload).to(ByteCode.aload_3).each(instruction -> {
            describe(ByteCode.toString(instruction), () -> {
                expect(configuration.getDecompilerExtension(context, instruction)).not().toBe(equalTo(null));
            });
        });

        Range.from(ByteCode.istore).to(ByteCode.astore_3).each(instruction -> {
            describe(ByteCode.toString(instruction), () -> {
                expect(configuration.getDecompilerExtension(context, instruction)).not().toBe(equalTo(null));
            });
        });
    }

    @Test
    public void configurationShouldSupportLoadWithIndexInCode() throws IOException {
        final Object[][] args = new Object[][] {
                { ByteCode.iload, int.class },
                { ByteCode.lload, long.class },
                { ByteCode.fload, float.class },
                { ByteCode.dload, double.class },
                { ByteCode.aload, Object.class }
        };

        for (Object[] arg : args) {
            final int instruction = (Integer) arg[0];
            final Type type = (Type) arg[1];
            final int index = 123;

            when(localVariableTable.getLocalVariables()).thenReturn(Arrays.asList(new LocalVariableImpl(-1, -1, "foo", type, index)));

            decompile(instruction, index);

            verify(context, times(1)).push(eq(AST.local("foo", type, index)));
        }
    }

    @Test
    public void configureShouldSupportStoreWithIndexInCode() throws IOException {
        final Object[][] args = new Object[][] {
                { ByteCode.istore, int.class },
                { ByteCode.lstore, long.class },
                { ByteCode.fstore, float.class },
                { ByteCode.dstore, double.class },
                { ByteCode.astore, Object.class }
        };

        for (Object[] arg : args) {
            final int instruction = (Integer) arg[0];
            final Type type = (Type) arg[1];
            final int index = 123;

            when(localVariableTable.getLocalVariables()).thenReturn(Arrays.asList(new LocalVariableImpl(-1, -1, "foo", type, index)));
            when(context.pop()).thenReturn(AST.constant(1));


            decompile(instruction, index);

            verify(context, times(1)).enlist(eq(AST.set(index, "foo", type, AST.constant(1))));
        }
    }

    @Test
    public void iloadnInstructionsShouldBeConfigured() throws IOException {
        assertLoadNInstructionsConfigured(int.class, ByteCode.iload_0, ByteCode.iload_3);
    }

    @Test
    public void floadnInstructionsShouldBeConfigured() throws IOException {
        assertLoadNInstructionsConfigured(float.class, ByteCode.fload_0, ByteCode.fload_3);
    }

    @Test
    public void dloadnInstructionsShouldBeConfigured() throws IOException {
        assertLoadNInstructionsConfigured(double.class, ByteCode.dload_0, ByteCode.dload_3);
    }

    @Test
    public void lloadnInstructionsShouldBeConfigured() throws IOException {
        assertLoadNInstructionsConfigured(long.class, ByteCode.lload_0, ByteCode.lload_3);
    }

    @Test
    public void aloadnInstructionsShouldBeConfigured() throws IOException {
        assertLoadNInstructionsConfigured(Object.class, ByteCode.aload_0, ByteCode.aload_3);
    }

    @Test
    public void istorenInstructionsShouldBeConfigured() throws IOException {
        assertStoreNInstructionsCanBeConfigured(int.class, ByteCode.istore_0, ByteCode.istore_3);
    }

    @Test
    public void fstorenInstructionsShouldBeConfigured() throws IOException {
        assertStoreNInstructionsCanBeConfigured(float.class, ByteCode.fstore_0, ByteCode.fstore_3);
    }

    @Test
    public void dstorenInstructionsShouldBeConfigured() throws IOException {
        assertStoreNInstructionsCanBeConfigured(double.class, ByteCode.dstore_0, ByteCode.dstore_3);
    }

    @Test
    public void lstorenInstructionsShouldBeConfigured() throws IOException {
        assertStoreNInstructionsCanBeConfigured(long.class, ByteCode.lstore_0, ByteCode.lstore_3);
    }

    @Test
    public void astorenInstructionsShouldBeConfigured() throws IOException {
        assertStoreNInstructionsCanBeConfigured(Object.class, ByteCode.astore_0, ByteCode.astore_3);
    }

    private void assertStoreNInstructionsCanBeConfigured(Class<?> expectedType, int base, int end) {
        Range.from(base).to(end).each(n -> {
            final int index = n - base;

            when(localVariableTable.getLocalVariables()).thenReturn(Arrays.asList(new LocalVariableImpl(-1, -1, "foo", expectedType, index)));
            when(context.pop()).thenReturn(AST.constant(1));

            decompile(n);
            verify(context, times(1)).enlist(eq(AST.set(index, "foo", expectedType, AST.constant(1))));
        });
    }

    private void assertLoadNInstructionsConfigured(Class<?> expectedType, int base, int end) {
        Range.from(base).to(end).each(n -> {
            final int index = n - base;
            when(localVariableTable.getLocalVariables()).thenReturn(Arrays.asList(new LocalVariableImpl(-1, -1, "foo", expectedType, index)));
            decompile(n);
            verify(context, times(1)).push(eq(AST.local("foo", expectedType, index)));
        });
    }

    private void decompile(int instruction, int ... code) {
        final byte[] codeBuffer = new byte[code.length];

        for (int i = 0; i < code.length; i++) {
            codeBuffer[i] = (byte) code[i];
        }

        try {
            final DecompilerDelegate extension = configuration.getDecompilerExtension(context, instruction);

            expect(extension).not().toBe(equalTo(null));

            extension.apply(context, new InputStreamCodeStream(new ByteArrayInputStream(codeBuffer)), instruction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}