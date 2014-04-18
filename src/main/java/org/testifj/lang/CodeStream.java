package org.testifj.lang;

import java.io.IOException;

public interface CodeStream {

    int nextInstruction() throws IOException;

    int peekInstruction() throws IOException;

    int nextByte() throws IOException;

    int peekByte() throws IOException;

    int nextUnsignedByte() throws IOException;

    int peekUnsignedByte() throws IOException;

    int peekUnsignedShort() throws IOException;

    int nextUnsignedShort() throws IOException;

    /**
     * Commit to the peeked result. The buffered data accumulated to enable reset will be discarded and
     * the PC will be advanced to the new location. Note that the PC will be forwarded through all
     * instructions, i.e. if the PC is advanced n instructions, it will be called n times.
     */
    void commit();

}
