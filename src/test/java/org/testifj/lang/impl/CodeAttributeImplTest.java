package org.testifj.lang.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.testifj.lang.Attribute;
import org.testifj.lang.ExceptionTableEntry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class CodeAttributeImplTest {

    private final ByteBuffer buffer = mock(ByteBuffer.class);
    private final List<ExceptionTableEntry> emptyExceptionTable = Collections.<ExceptionTableEntry>emptyList();
    private final List<Attribute> emptyAttributes = Collections.<Attribute>emptyList();

    @Test
    public void constructorShouldNotAcceptNullAttributeData() {
        expect(() -> new CodeAttributeImpl(null, 1, 2, buffer, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxStack() {
        expect(() -> new CodeAttributeImpl(buffer, -1, 1, buffer, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxLocals() {
        expect(() -> new CodeAttributeImpl(buffer, 1, -1, buffer, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullByteCode() {
        expect(() -> new CodeAttributeImpl(buffer, 1, 1, null, emptyExceptionTable, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullExceptionTable() {
        expect(() -> new CodeAttributeImpl(buffer, 1, 2, buffer, null, emptyAttributes)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullAttributes() {
        expect(() -> new CodeAttributeImpl(buffer, 1, 2, buffer, emptyExceptionTable, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainProvidedParameters() throws IOException {
        final byte[] attributeData = {1, 2, 3};
        final byte[] byteCode = {2, 3, 4};
        final ExceptionTableEntry exceptionTableEntry = mock(ExceptionTableEntry.class);
        final Attribute codeAttribute = mock(Attribute.class);
        final CodeAttributeImpl attribute = new CodeAttributeImpl(ByteBuffer.wrap(attributeData), 1, 2, ByteBuffer.wrap(byteCode), Arrays.asList(exceptionTableEntry), Arrays.asList(codeAttribute));

        expect(IOUtils.toByteArray(attribute.getData())).to(equal(attributeData));
        expect(IOUtils.toByteArray(attribute.getData())).to(equal(attributeData));
        expect(attribute.getMaxStack()).to(equal(1));
        expect(attribute.getMaxLocals()).to(equal(2));
        expect(IOUtils.toByteArray(attribute.getCode())).to(equal(byteCode));
        expect(IOUtils.toByteArray(attribute.getCode())).to(equal(byteCode));
        expect(attribute.getExceptionTable().toArray()).toBe(new Object[]{exceptionTableEntry});
        expect(attribute.getAttributes().toArray()).toBe(new Object[]{codeAttribute});
    }

}
