package org.testifj.lang.impl;

import org.junit.Test;
import org.testifj.lang.CodeGenerationContext;
import org.testifj.lang.impl.CodeGenerationContextImpl;

import static org.testifj.Expect.expect;

public class CodeGenerationContextTest {

    @Test
    public void indentationLevelShouldInitiallyBeZero() {
        expect(new CodeGenerationContextImpl().getIndentationLevel()).toBe(0);
    }

    @Test
    public void subSectionShouldIncreaseIndentationLevel() {
        final CodeGenerationContext root = new CodeGenerationContextImpl();
        final CodeGenerationContext subSection = root.subSection();

        expect(subSection.getIndentationLevel()).toBe(1);
    }

}
