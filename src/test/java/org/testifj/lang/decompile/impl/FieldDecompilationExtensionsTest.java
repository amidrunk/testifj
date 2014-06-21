package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.FieldRefDescriptorImpl;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.ConstantPool;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.impl.DecompilerConfigurationImpl;
import org.testifj.lang.decompile.impl.FieldDecompilationExtensions;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.FieldAssignmentImpl;
import org.testifj.lang.model.impl.FieldReferenceImpl;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.CodeStreamTestUtils.codeStream;
import static org.testifj.lang.decompile.impl.FieldDecompilationExtensions.putfield;
import static org.testifj.lang.decompile.impl.FieldDecompilationExtensions.putstatic;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

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
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        FieldDecompilationExtensions.configure(configurationBuilder);

        given(configurationBuilder.build()).then(it -> {
            expect(it.getDecompilerExtension(mock(DecompilationContext.class), ByteCode.putfield)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(mock(DecompilationContext.class), ByteCode.putstatic)).not().toBe(equalTo(null));
        });
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
