package org.testifj.lang.decompile.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testifj.lang.CodeStreamTestUtils;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.ClassFile;
import org.testifj.lang.classfile.ClassFileFormatException;
import org.testifj.lang.classfile.Method;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.AST;
import org.testifj.lang.model.Constant;
import org.testifj.lang.model.impl.ConstantImpl;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;

public class ConstantDecompilerDelegationTest {

    private final ConstantDecompilerDelegation delegation = new ConstantDecompilerDelegation();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final Method method = mock(Method.class);
    private final ClassFile classFile = mock(ClassFile.class);
    private final ConstantPool constantPool = mock(ConstantPool.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Before
    public void setup() {
        when(decompilationContext.getMethod()).thenReturn(method);
        when(method.getClassFile()).thenReturn(classFile);
        when(classFile.getConstantPool()).thenReturn(constantPool);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        expect(() -> delegation.configure(null)).toThrow(AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForLoadConstantInstructionsFromConstantPool() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.ldc)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.ldcw)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.ldc2w)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void configureShouldConfigureSupportForPushingNullConstant() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.aconst_null)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void configureShouldConfigureSupportForStaticDoubleConstants() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dconst_0)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.dconst_1)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void configureShouldConfigureSupportForStaticFloatConstants() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.fconst_0)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.fconst_1)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.fconst_2)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void configureShouldConfigureSupportForIntegerConstants() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_m1)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_0)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_1)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_2)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_3)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_4)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.iconst_5)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void configureShouldConfigureSupportForLongConstants() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.lconst_0)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.lconst_1)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void configureShouldConfigureSupportForConstantPushInstructions() {
        given(configuration()).then(it -> {
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.bipush)).not().toBe(equalTo(null));
            expect(it.getDecompilerExtension(decompilationContext, ByteCode.sipush)).not().toBe(equalTo(null));
        });
    }

    @Test
    public void bipushShouldPushSignExtendedByte() throws IOException {
        when(codeStream.nextByte()).thenReturn(100);
        execute(ByteCode.bipush);
        verify(decompilationContext).push(eq(new ConstantImpl(100, int.class)));
    }

    @Test
    public void sipushShouldPushSignExtendedShort() throws IOException {
        when(codeStream.nextSignedShort()).thenReturn(1234);
        execute(ByteCode.sipush);
        verify(decompilationContext).push(eq(new ConstantImpl(1234, int.class)));
    }

    @Test
    public void lconstInstructionsShouldPushLong() throws IOException {
        execute(ByteCode.lconst_0);
        execute(ByteCode.lconst_1);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0L, long.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1L, int.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void iconstInstructionsShouldPushInteger() throws IOException {
        execute(ByteCode.iconst_m1);
        execute(ByteCode.iconst_0);
        execute(ByteCode.iconst_1);
        execute(ByteCode.iconst_2);
        execute(ByteCode.iconst_3);
        execute(ByteCode.iconst_4);
        execute(ByteCode.iconst_5);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(-1, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(2, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(3, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(4, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(5, int.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void fconstInstructionsShouldPushFloat() throws IOException {
        execute(ByteCode.fconst_0);
        execute(ByteCode.fconst_1);
        execute(ByteCode.fconst_2);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0f, float.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1f, float.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(2f, float.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void dconstInstructionsShouldPushDouble() throws IOException {
        execute(ByteCode.dconst_0);
        execute(ByteCode.dconst_1);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0d, double.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1d, double.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void aconst_nullShouldPushNull() throws IOException {
        execute(ByteCode.aconst_null);

        verify(decompilationContext).push(new ConstantImpl(null, Object.class));
    }

    @Test
    public void ldcCanPushConstantIntegerFromConstantPool() throws Exception {
        verifyLDC(99, new ConstantPoolEntry.IntegerEntry(1234), AST.constant(1234));
    }

    @Test
    public void ldcCanPushConstantFloatFromConstantPool() throws Exception {
        verifyLDC(99, new ConstantPoolEntry.FloatEntry(1234f), AST.constant(1234f));
    }

    @Test
    public void ldcCanPushConstantStringFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("foo");
        verifyLDC(99, new ConstantPoolEntry.StringEntry(101), AST.constant("foo"));
    }

    @Test
    public void ldcCanPushConstantClassFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("java/lang/String");
        when(decompilationContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        verifyLDC(99, new ConstantPoolEntry.ClassEntry(101), AST.constant(String.class));
    }

    @Test
    public void ldcShouldFailForInvalidConstantPoolEntry() throws Exception {
        expect(() -> verifyLDC(99, new ConstantPoolEntry.DoubleEntry(1234d), AST.constant(1234d))).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void ldcwCanPushConstantIntegerFromConstantPool() throws Exception {
        verifyLDCW(1, 99, new ConstantPoolEntry.IntegerEntry(1234), AST.constant(1234));
    }

    @Test
    public void ldcwCanPushConstantFloatFromConstantPool() throws Exception {
        verifyLDCW(1, 99, new ConstantPoolEntry.FloatEntry(1234f), AST.constant(1234f));
    }

    @Test
    public void ldcwCanPushConstantStringFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("foo");
        verifyLDCW(1, 99, new ConstantPoolEntry.StringEntry(101), AST.constant("foo"));
    }

    @Test
    public void ldcwCanPushConstantClassFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("java/lang/String");
        when(decompilationContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        verifyLDCW(1, 99, new ConstantPoolEntry.ClassEntry(101), AST.constant(String.class));
    }

    @Test
    public void ldcwShouldFailForInvalidConstantPoolEntry() throws Exception {
        expect(() -> verifyLDCW(1, 99, new ConstantPoolEntry.DoubleEntry(1234d), AST.constant(1234d))).toThrow(ClassFileFormatException.class);
    }

    @Test
    public void ldc2wCanPushDoubleFromConstantPool() throws Exception {
        verifyLDC2W(1, 100, new ConstantPoolEntry.DoubleEntry(1234d), AST.constant(1234d));
    }

    @Test
    public void ldc2wCanPushLongFromConstantPool() throws Exception {
        verifyLDC2W(1, 100, new ConstantPoolEntry.LongEntry(1234L), AST.constant(1234L));
    }

    @Test
    public void ldc2wShouldFailForInvalidConstantPoolEntry() throws Exception {
        expect(() -> verifyLDC2W(1, 99, new ConstantPoolEntry.IntegerEntry(1234), AST.constant(1234d))).toThrow(ClassFileFormatException.class);
    }

    private void verifyLDC(int index, ConstantPoolEntry entry, Constant constant) throws IOException {
        when(constantPool.getEntry(eq(index))).thenReturn(entry);

        configuration().getDecompilerExtension(decompilationContext, ByteCode.ldc).apply(decompilationContext, CodeStreamTestUtils.codeStream(index), ByteCode.ldc);

        verify(decompilationContext).push(constant);
    }

    private void verifyLDCW(int indexh, int indexl, ConstantPoolEntry entry, Constant constant) throws IOException {
        when(constantPool.getEntry(eq(indexh << 8 | indexl))).thenReturn(entry);

        configuration().getDecompilerExtension(decompilationContext, ByteCode.ldcw).apply(decompilationContext, CodeStreamTestUtils.codeStream(indexh, indexl), ByteCode.ldcw);

        verify(decompilationContext).push(constant);
    }

    private void verifyLDC2W(int indexh, int indexl, ConstantPoolEntry entry, Constant constant) throws IOException {
        when(constantPool.getEntry(eq(indexh << 8 | indexl))).thenReturn(entry);

        configuration().getDecompilerExtension(decompilationContext, ByteCode.ldc2w).apply(decompilationContext, CodeStreamTestUtils.codeStream(indexh, indexl), ByteCode.ldc2w);

        verify(decompilationContext).push(constant);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationImpl.Builder builder = new DecompilerConfigurationImpl.Builder();

        delegation.configure(builder);

        return builder.build();
    }

    private void execute(int instruction) throws IOException {
        configuration().getDecompilerExtension(decompilationContext, instruction).apply(decompilationContext, codeStream, instruction);
    }

}