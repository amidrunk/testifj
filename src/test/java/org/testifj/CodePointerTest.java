package org.testifj;

import org.junit.Test;
import org.testifj.lang.Method;
import org.testifj.lang.model.Element;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class CodePointerTest {

    private final Element element = mock(Element.class);
    private final Method method = mock(Method.class);

    @Test
    public void constructorShouldValidateArguments() {
        expect(() -> new CodePointer(null, element)).toThrow(AssertionError.class);
        expect(() -> new CodePointer(method, null)).toThrow(AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final CodePointer codePointer = new CodePointer(method, element);

        expect(codePointer.getMethod()).toBe(method);
        expect(codePointer.getElement()).toBe(element);
    }

    @Test
    public void forElementShouldNotAcceptNullElement() {
        final CodePointer codePointer = new CodePointer(method, element);

        expect(() -> codePointer.forElement(null)).toThrow(AssertionError.class);
    }

    @Test
    public void forElementShouldReturnPointerToElementInOriginalContext() {
        final CodePointer originalPointer = new CodePointer(method, element);
        final Element newElement = mock(Element.class);
        final CodePointer newCodePointer = originalPointer.forElement(newElement);

        expect(newCodePointer.getElement()).toBe(newElement);
        expect(newCodePointer.getMethod()).toBe(method);
    }

}
