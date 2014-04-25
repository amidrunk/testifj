package org.testifj.lang;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;

public class ByteCodeSelectorTest {

    @Test
    public void forByteCodeShouldNotAcceptInvalidByteCode() {
        expect(() -> ByteCodeSelector.forByteCode(0xFFF)).toThrow(AssertionError.class);
        expect(() -> ByteCodeSelector.forByteCode(-1)).toThrow(AssertionError.class);
    }

    @Test
    public void selectorForByteCodeShouldAlwaysSelectMatchingByteCode() {
        given(ByteCodeSelector.forByteCode(ByteCode.aaload)).then(selector -> {
            expect(selector.getByteCode()).toBe(ByteCode.aaload);
            expect(selector.select(mock(DecompilationContext.class), ByteCode.aaload)).toBe(true);
        });
    }

    @Test
    public void selectorForByteCodeShouldNotSelectDifferentByteCode() {
        given(ByteCodeSelector.forByteCode(ByteCode.aaload)).then(selector -> {
            expect(selector.select(mock(DecompilationContext.class), ByteCode.astore)).toBe(false);
        });
    }

}
