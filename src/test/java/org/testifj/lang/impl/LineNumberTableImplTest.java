package org.testifj.lang.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.testifj.lang.LineNumberTableEntry;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.testifj.Expect.expect;

public class LineNumberTableImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        expect(() -> new LineNumberTableImpl(null, new LineNumberTableEntry[0])).toThrow(AssertionError.class);
        expect(() -> new LineNumberTableImpl(ByteBuffer.wrap(new byte[]{1, 2}), null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() throws IOException {
        final byte[] data = {1, 2, 3};
        final LineNumberTableEntry entry = Mockito.mock(LineNumberTableEntry.class);
        final LineNumberTableImpl table = new LineNumberTableImpl(ByteBuffer.wrap(data), new LineNumberTableEntry[]{entry});

        expect(IOUtils.toByteArray(table.getData())).toBe(data);
        expect(table.getEntries().toArray()).toBe(new Object[]{entry});
    }

}
