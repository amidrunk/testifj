package org.testifj.lang.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.FieldAssignmentImpl;
import org.testifj.lang.model.impl.FieldReferenceImpl;
import sun.jvm.hotspot.interpreter.Bytecode;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.lang.CodeStreamTestUtils.codeStream;
import static org.testifj.lang.impl.FieldDecompilationExtensions.putfield;
import static org.testifj.lang.impl.FieldDecompilationExtensions.putstatic;

public class FieldDecompilationExtensionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final Method exampleMethod = mock(Method.class);
    private final ClassFile exampleClassFile = mock(ClassFile.class);
    private final ConstantPool constantPool = mock(ConstantPool.class);

    @Before
    public void setup() {
        when(context.getMethod()).thenReturn(exampleMethod);
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleClassFile.getConstantPool()).thenReturn(constantPool);

        doAnswer(i -> new SimpleTypeResolver().resolveType(((String) i.getArguments()[0]).replace('/', '.')))
                .when(context).resolveType(anyString());
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> FieldDecompilationExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForByteCodes() {
        final DecompilerConfiguration.Builder configurationBuilder = mock(DecompilerConfiguration.Builder.class);

        FieldDecompilationExtensions.configure(configurationBuilder);

        verify(configurationBuilder).extend(eq(ByteCode.putfield), any(DecompilerExtension.class));
        verify(configurationBuilder).extend(eq(ByteCode.putstatic), any(DecompilerExtension.class));
    }

    @Test
    public void putfieldExtensionShouldPopOperandsAndPushAssignment() throws IOException {
        final Expression assignedFieldValue = AST.constant("foo");
        final Expression declaringInstance = AST.constant("bar");

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/Integer", "Ljava/lang/String;", "foo"));
        when(context.pop()).thenReturn(assignedFieldValue, declaringInstance);

        putfield().decompile(context, codeStream(0, 1), ByteCode.putfield);

        verify(context).enlist(eq(new FieldAssignmentImpl(
                new FieldReferenceImpl(declaringInstance, Integer.class, String.class, "foo"),
                assignedFieldValue)
        ));
    }

    @Test
    public void putstaticExtensionShouldPopValueAndPushAssignment() throws IOException {
        final Constant value = AST.constant("foo");

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/Integer", "Ljava/lang/String;", "foo"));
        when(context.pop()).thenReturn(value);

        putstatic().decompile(context, codeStream(0, 1), ByteCode.putstatic);

        verify(context).enlist(eq(new FieldAssignmentImpl(
                new FieldReferenceImpl(null, Integer.class, String.class, "foo"),
                value
        )));
    }

}
