package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.ClassModelTestUtils;
import org.testifj.CodePointer;
import org.testifj.CodePointerImpl;
import org.testifj.lang.*;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.NewInstance;
import org.testifj.lang.model.VariableAssignment;
import org.testifj.lang.model.impl.AllocateInstanceImpl;
import org.testifj.lang.model.impl.ConstantImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;

public class NewExtensionsTest {

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> NewExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForNewByteCode() {
        final DecompilerConfiguration.Builder configurationBuilder = mock(DecompilerConfiguration.Builder.class);

        NewExtensions.configure(configurationBuilder);

        verify(configurationBuilder).extend(eq(ByteCode.new_), any());
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

        NewExtensions.newInstance().decompile(context, codeStream, ByteCode.new_);

        verify(context).push(eq(new AllocateInstanceImpl(String.class)));
        verify(context).resolveType(eq("java/lang/String"));
    }

    @Test
    public void newByteCodeShouldBeSupportedInByteCode() {
        final String str = new String("str");

        final CodePointer[] codePointers = ClassModelTestUtils.codeForLineOffset(-2);
        expect(codePointers.length).toBe(1);

        final Element element = codePointers[0].getElement();
        expect(element.getElementType()).toBe(ElementType.VARIABLE_ASSIGNMENT);

        final VariableAssignment variableAssignment = (VariableAssignment) element;
        expect(variableAssignment.getVariableName()).toBe("str");
        expect(variableAssignment.getType()).toBe(String.class);
        expect(variableAssignment.getValue().getElementType()).toBe(ElementType.NEW);

        final NewInstance newInstance = (NewInstance) variableAssignment.getValue();

        expect(newInstance.getType()).toBe(String.class);
        expect(newInstance.getParameters().toArray()).toBe(new Object[]{new ConstantImpl("str", String.class)});
    }

}
