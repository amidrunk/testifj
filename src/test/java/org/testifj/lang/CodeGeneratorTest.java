package org.testifj.lang;

import org.junit.Test;
import org.testifj.BasicDescription;
import org.testifj.Description;
import org.testifj.lang.codegeneration.CodeGenerator;

import static org.testifj.Expect.expect;

@SuppressWarnings("unchecked")
public class CodeGeneratorTest {

    @Test
    public void describeShouldGenerateCodeAndReturnTextDescription() {
        final CodeGenerator codeGenerator = (e, out) -> out.print("bar");
        final Description description = codeGenerator.describe("foo");

        expect(description).toBe(BasicDescription.from("bar"));
    }

}
