package org.testifj;

import org.junit.Test;
import org.testifj.lang.Method;
import org.testifj.lang.model.Element;
import org.testifj.lang.model.impl.ReturnImpl;

import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;

public class CodeDescriberTest {

    private final CodeDescriber describer = new CodeDescriber();
    private final Method method = mock(Method.class);

    @Test
    public void constructorShouldNotAcceptNullCodeDescriber() {
        expect(() -> new CodeDescriber(null)).toThrow(AssertionError.class);
    }

    @Test
    public void returnCanBeDescribed() {
        final Description description = describer.describe(pointer(new ReturnImpl()));

        expect(description).toBe(BasicDescription.from("return"));
    }

    private CodePointer pointer(Element element) {
        return new CodePointer(method, element);
    }

}
