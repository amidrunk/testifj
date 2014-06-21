package org.testifj.lang.classfile.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.testifj.lang.classfile.*;
import org.testifj.lang.classfile.impl.CodeAttributeImpl;
import org.testifj.lang.classfile.impl.LocalVariableTableImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testifj.Expect.expect;

public class CodeAttributeImplTest {

    private final ByteBuffer buffer = mock(ByteBuffer.class);

    private final List<ExceptionTableEntry> emptyExceptionTable = Collections.<ExceptionTableEntry>emptyList();

    private final List<Attribute> emptyAttributes = Collections.<Attribute>emptyList();

    @Before
    public void setup() {
        when(buffer.asReadOnlyBuffer()).thenReturn(buffer);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxStack() {
        expect(() -> new CodeAttributeImpl(-1, 1, buffer, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxLocals() {
        expect(() -> new CodeAttributeImpl(1, -1, buffer, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullByteCode() {
        expect(() -> new CodeAttributeImpl(1, 1, null, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullExceptionTable() {
        expect(() -> new CodeAttributeImpl(1, 2, buffer, null, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullAttributes() {
        expect(() -> new CodeAttributeImpl(1, 2, buffer, emptyExceptionTable, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainProvidedParameters() throws IOException {
        final byte[] byteCode = {2, 3, 4};
        final ExceptionTableEntry exceptionTableEntry = mock(ExceptionTableEntry.class);
        final Attribute codeAttribute = mock(Attribute.class);
        final CodeAttributeImpl attribute = new CodeAttributeImpl(1, 2, ByteBuffer.wrap(byteCode), Arrays.asList(exceptionTableEntry), Arrays.asList(codeAttribute));

        expect(attribute.getMaxStack()).toBe(1);
        expect(attribute.getMaxLocals()).toBe(2);
        expect(IOUtils.toByteArray(attribute.getCode())).toBe(byteCode);
        expect(attribute.getExceptionTable().toArray()).toBe(new Object[]{exceptionTableEntry});
        expect(attribute.getAttributes().toArray()).toBe(new Object[]{codeAttribute});
    }

    @Test
    public void withLocalAttributeTableShouldNotAcceptNullTable() throws Exception {
        final CodeAttributeImpl attribute = new CodeAttributeImpl(0, 0, buffer, emptyExceptionTable, emptyAttributes);

        expect(() -> attribute.withLocalVariableTable(null)).toThrow(AssertionError.class);
    }

    @Test
    public void withLocalAttributeTableShouldAddAttributeIfNotExists() throws Exception {
        final Attribute otherAttribute = mock(Attribute.class);
        final LocalVariableTable newTable = mock(LocalVariableTable.class);

        final CodeAttribute newAttribute = new CodeAttributeImpl(0, 0, buffer, emptyExceptionTable, Arrays.asList(otherAttribute))
                .withLocalVariableTable(newTable);

        expect(newAttribute.getAttributes().toArray()).toBe(new Object[]{otherAttribute, newTable});
    }

    @Test
    public void withLocalAttributeTableShouldReplaceExistingAttributeTable() throws Exception {
        final Attribute otherAttribute = mock(Attribute.class);
        final LocalVariableTable oldTable = new LocalVariableTableImpl(new LocalVariable[0]);
        final LocalVariableTable newTable = mock(LocalVariableTable.class);
        final CodeAttribute attr = new CodeAttributeImpl(0, 0, buffer, emptyExceptionTable, Arrays.asList(oldTable, otherAttribute))
                .withLocalVariableTable(newTable);

        expect(attr.getAttributes().toArray()).toBe(new Object[]{otherAttribute, newTable});
    }

}
