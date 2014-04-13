package org.testifj.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.testifj.Expect.expect;
import static org.testifj.matchers.core.Equal.equal;

public class ByteBufferInputStreamTest {

    @Test
    public void constructorShouldNotAcceptNullByteBuffer() {
        expect(() -> new ByteBufferInputStream(null)).toThrow(AssertionError.class);
    }

    @Test
    public void entireByteBufferCanBeRead() throws Exception {
        final byte[] originalArray = {1, 2, 3, 4};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        expect(IOUtils.toByteArray(in)).to(equal(originalArray));
    }

    @Test
    public void availableShouldReturnRemainingNumberOfBytes() {
        final byte[] originalArray = {1, 2, 3, 4};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        expect(in.available()).to(equal(4));
        expect(in.read()).to(equal(1));
        expect(in.available()).to(equal(3));
        expect(in.read()).to(equal(2));
    }
}
