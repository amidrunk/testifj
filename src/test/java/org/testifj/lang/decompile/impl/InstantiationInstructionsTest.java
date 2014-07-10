package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.Caller;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.DefaultConstantPool;
import org.testifj.lang.decompile.CodePointer;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.ConstantPoolEntry;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.ProgramCounter;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.NewInstance;
import org.testifj.lang.model.VariableAssignment;
import org.testifj.lang.model.impl.InstanceAllocationImpl;
import org.testifj.lang.model.impl.ConstantImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.ClassModelTestUtils.code;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class InstantiationInstructionsTest {

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> new InstantiationInstructions().configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForNewByteCode() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.new_)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void newInstanceShouldPushInstanceAllocationOntoStack() throws IOException {
        final DecompilationContext context = mock(DecompilationContext.class);
        final InputStreamCodeStream codeStream = new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) 0, (byte) 1, (byte) ByteCode.dup}), mock(ProgramCounter.class));

        final Method method = mock(Method.class);
        final ClassFile classFile = mock(ClassFile.class);

        when(classFile.getConstantPool()).thenReturn(new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.ClassEntry(2))
                .addEntry(new ConstantPoolEntry.UTF8Entry("java/lang/String")).create());

        when(method.getClassFile()).thenReturn(classFile);
        when(context.getMethod()).thenReturn(method);
        when(context.resolveType(eq("java/lang/String"))).thenReturn(String.class);

        InstantiationInstructions.newInstance().apply(context, codeStream, ByteCode.new_);

        verify(context).push(eq(new InstanceAllocationImpl(String.class)));
        verify(context).resolveType(eq("java/lang/String"));
    }

    @Test
    public void newByteCodeShouldBeSupportedInByteCode() {
        final String str = new String("str");

        final CodePointer[] codePointers = code(Caller.adjacent(-2));
        expect(codePointers.length).toBe(1);

        final Element element = codePointers[0].getElement();
        expect(element.getElementType()).toBe(ElementType.VARIABLE_ASSIGNMENT);

        final VariableAssignment variableAssignment = (VariableAssignment) element;
        expect(variableAssignment.getVariableName()).toBe("str");
        expect(variableAssignment.getValue().getElementType()).toBe(ElementType.NEW);

        final NewInstance newInstance = (NewInstance) variableAssignment.getValue();

        expect(newInstance.getType()).toBe(String.class);
        expect(newInstance.getParameters().toArray()).toBe(new Object[]{new ConstantImpl("str", String.class)});
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        new InstantiationInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }

}
