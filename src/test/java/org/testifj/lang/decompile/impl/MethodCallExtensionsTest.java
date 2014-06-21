package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.classfile.impl.DefaultConstantPool;
import org.testifj.lang.classfile.impl.SimpleTypeResolver;
import org.testifj.lang.decompile.*;
import org.testifj.lang.decompile.impl.*;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.ConstantImpl;
import org.testifj.lang.model.impl.LocalVariableReferenceImpl;
import org.testifj.lang.model.impl.MethodCallImpl;
import org.testifj.lang.model.impl.MethodSignature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class MethodCallExtensionsTest {

    private final Decompiler decompiler = mock(Decompiler.class);
    private final Method method = mock(Method.class);
    private final ClassFile classFile = mock(ClassFile.class);
    private final ProgramCounterImpl pc = new ProgramCounterImpl(-1);
    private final TypeResolver typeResolver = new SimpleTypeResolver();
    private final LineNumberCounter lineNumberCounter = mock(LineNumberCounter.class);
    private final DecompilationContext context = new DecompilationContextImpl(decompiler, method, pc, lineNumberCounter,typeResolver);

    @Before
    public void setup() {
        when(method.getClassFile()).thenReturn(classFile);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> MethodCallExtensions.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForMethodCalls() {
        final DecompilerConfiguration.Builder builder = new DecompilerConfigurationImpl.Builder();

        MethodCallExtensions.configure(builder);

        given(builder.build()).then(it -> {
            expect(it.getDecompilerExtension(context, ByteCode.invokeinterface)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(context, ByteCode.invokespecial)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void invokeInterfaceWithNoArgsAndNoReturnShouldPushMethodCall() {
        final ConstantPool constantPool = interfaceMethodRefPool("java/lang/String", "myMethod", "()V");
        final LocalVariableReferenceImpl instance = new LocalVariableReferenceImpl("myVariable", String.class, 1);

        context.push(instance);

        decompile(constantPool, in(ByteCode.invokeinterface, 0, 1, 1), MethodCallExtensions.invokeinterface());

        expect(context.getStackedExpressions().toArray()).toBe(new Object[]{
                new MethodCallImpl(
                        String.class,
                        "myMethod",
                        MethodSignature.parse("()V"),
                        instance,
                        new Expression[0])
        });
    }

    @Test
    public void invokeInterfaceShouldFailIfSubsequentByteIsZero() {
        final ConstantPool constantPool = interfaceMethodRefPool("java/lang/String", "myMethod", "()V");

        context.push(new ConstantImpl("foo", String.class));

        expect(() -> {
            decompile(constantPool, in(ByteCode.invokeinterface, 0, 1, 0), MethodCallExtensions.invokeinterface());
        }).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void invokeInterfaceWithArgumentsShouldPushMethodCallWithArgs() {
        final ConstantPool constantPool = interfaceMethodRefPool("java/lang/String", "myMethod", "(Ljava/lang/String;)V");

        final LocalVariableReferenceImpl instance = new LocalVariableReferenceImpl("this", String.class, 0);
        final ConstantImpl arg1 = new ConstantImpl(1234, int.class);

        context.push(instance);
        context.push(arg1);

        decompile(constantPool, in(ByteCode.invokeinterface, 0, 1, 1), MethodCallExtensions.invokeinterface());

        expect(context.getStackedExpressions().toArray()).toBe(new Object[]{
                new MethodCallImpl(
                        String.class,
                        "myMethod",
                        MethodSignature.parse("(Ljava/lang/String;)V"),
                        instance,
                        new Expression[]{arg1})
        });
    }

    private DefaultConstantPool interfaceMethodRefPool(String declaringClass, String name, String signature) {
        return new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.InterfaceMethodRefEntry(2, 3))
                .addEntry(new ConstantPoolEntry.ClassEntry(4))
                .addEntry(new ConstantPoolEntry.NameAndTypeEntry(5, 6))
                .addEntry(new ConstantPoolEntry.UTF8Entry(declaringClass))
                .addEntry(new ConstantPoolEntry.UTF8Entry(name))
                .addEntry(new ConstantPoolEntry.UTF8Entry(signature))
                .create();
    }

    private DecompilationContext decompile(ConstantPool constantPool, InputStream in, DecompilerExtension extension) {
        when(classFile.getConstantPool()).thenReturn(constantPool);

        final InputStreamCodeStream cs = new InputStreamCodeStream(in, pc);

        try {
            extension.decompile(context, cs, cs.nextInstruction());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return context;
    }

    private InputStream in(int ... byteCodes) {
        final byte[] buf = new byte[byteCodes.length];

        for (int i = 0; i < byteCodes.length; i++) {
            buf[i] = (byte) byteCodes[i];
        }

        return new ByteArrayInputStream(buf);
    }
}