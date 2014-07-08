package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.CodeStreamTestUtils;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.FieldRefDescriptorImpl;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.decompile.CodeStream;
import org.testifj.lang.decompile.ConstantPool;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.LocalVariableReference;
import org.testifj.lang.model.impl.FieldAssignmentImpl;
import org.testifj.lang.model.impl.FieldReferenceImpl;
import org.testifj.util.SingleThreadedStack;
import org.testifj.util.Stack;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.lang.CodeStreamTestUtils.codeStream;
import static org.testifj.lang.decompile.impl.FieldInstructions.putfield;
import static org.testifj.lang.decompile.impl.FieldInstructions.putstatic;
import static org.testifj.matchers.core.IterableThatIs.iterableOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class FieldInstructionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final Method exampleMethod = mock(Method.class);
    private final ClassFile exampleClassFile = mock(ClassFile.class);
    private final ConstantPool constantPool = mock(ConstantPool.class);
    private final Stack<Expression> stack = new SingleThreadedStack<>();

    @Before
    public void setup() {
        when(context.getMethod()).thenReturn(exampleMethod);
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleClassFile.getConstantPool()).thenReturn(constantPool);
        when(context.getStack()).thenReturn(stack);

        doAnswer(i -> new SimpleTypeResolver().resolveType(((String) i.getArguments()[0]).replace('/', '.')))
                .when(context).resolveType(anyString());
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> new FieldInstructions().configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForByteCodes() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.putfield)).not().toBe(equalTo(null));
            expect(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.putstatic)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void putfieldExtensionShouldPopOperandsAndPushAssignment() throws IOException {
        final Expression assignedFieldValue = AST.constant("foo");
        final Expression declaringInstance = AST.constant("bar");

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/Integer", "Ljava/lang/String;", "foo"));
        when(context.pop()).thenReturn(assignedFieldValue, declaringInstance);

        putfield().apply(context, codeStream(0, 1), ByteCode.putfield);

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

        putstatic().apply(context, codeStream(0, 1), ByteCode.putstatic);

        verify(context).enlist(eq(new FieldAssignmentImpl(
                new FieldReferenceImpl(null, Integer.class, String.class, "foo"),
                value
        )));
    }

    @Test
    public void getfieldShouldPushFieldReference() throws IOException {
        final LocalVariableReference local = AST.local("foo", String.class, 1);

        stack.push(local);

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/String", "I", "bar"));

        execute(ByteCode.getfield, 0, 1);

        expect(stack).toBe(iterableOf(AST.field(local, int.class, "bar")));
    }

    @Test
    public void getstaticShouldPushFieldReference() throws IOException {
        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/String", "I", "bar"));

        execute(ByteCode.getstatic, 0, 1);

        expect(stack).toBe(iterableOf(AST.field(String.class, int.class, "bar")));
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(context, byteCode)
                .apply(context, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfiguration.Builder configurationBuilder = new DecompilerConfigurationImpl.Builder();

        new FieldInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }

}
