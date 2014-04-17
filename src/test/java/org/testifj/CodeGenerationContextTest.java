package org.testifj;

import org.junit.Test;

import static org.testifj.Expect.expect;

public class CodeGenerationContextTest {

    @Test
    public void indentationLevelShouldInitiallyBeZero() {
        expect(new CodeGenerationContext().getIndentationLevel()).toBe(0);
    }

    @Test
    public void subSectionShouldIncreaseIndentationLevel() {
        final CodeGenerationContext root = new CodeGenerationContext();
        final CodeGenerationContext subSection = root.subSection();

        expect(subSection.getIndentationLevel()).toBe(1);
    }

}
