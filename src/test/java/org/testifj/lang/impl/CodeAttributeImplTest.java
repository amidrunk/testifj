package org.testifj.lang.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class CodeAttributeImplTest {

    private final ByteBuffer buffer = mock(ByteBuffer.class);

    @Test
    public void constructorShouldNotAcceptNullAttributeData() {
        expect(() -> new CodeAttributeImpl(null, 1, 2, buffer)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxStack() {
        expect(() -> new CodeAttributeImpl(buffer, -1, 1, buffer)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxLocals() {
        expect(() -> new CodeAttributeImpl(buffer, 1, -1, buffer)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullByteCode() {
        expect(() -> new CodeAttributeImpl(buffer, 1, 1, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainProvidedParameters() throws IOException {
        final byte[] attributeData = {1, 2, 3};
        final byte[] byteCode = {2, 3, 4};
        final CodeAttributeImpl attribute = new CodeAttributeImpl(ByteBuffer.wrap(attributeData), 1, 2, ByteBuffer.wrap(byteCode));

        expect(IOUtils.toByteArray(attribute.getData())).to(equal(attributeData));
        expect(IOUtils.toByteArray(attribute.getData())).to(equal(attributeData));
        expect(attribute.getMaxStack()).to(equal(1));
        expect(attribute.getMaxLocals()).to(equal(2));
        expect(IOUtils.toByteArray(attribute.getCode())).to(equal(byteCode));
        expect(IOUtils.toByteArray(attribute.getCode())).to(equal(byteCode));
    }

}
