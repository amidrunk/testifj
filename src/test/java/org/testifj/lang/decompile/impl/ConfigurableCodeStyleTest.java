package org.testifj.lang.decompile.impl;

import org.junit.Test;
import org.testifj.lang.decompile.CodeStyle;
import org.testifj.lang.decompile.impl.ConfigurableCodeStyle;

import static org.testifj.Expect.expect;

public class ConfigurableCodeStyleTest {

    @Test
    public void codeStyleWithOmitThisCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setShouldOmitThis(true)
                .build();

        expect(codeStyle.shouldOmitThis()).toBe(true);
    }

    @Test
    public void codeStyleWithNotOmitThisCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setShouldOmitThis(false)
                .build();

        expect(codeStyle.shouldOmitThis()).toBe(false);
    }

    @Test
    public void codeStyleWithSimpleTypeNamesCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setUseSimpleClassNames(true)
                .build();

        expect(codeStyle.getTypeName(String.class)).toBe("String");
    }

    @Test
    public void codeStyleWithQualifiedTypeNamesCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setUseSimpleClassNames(false)
                .build();

        expect(codeStyle.getTypeName(String.class)).toBe("java.lang.String");
    }

    @Test
    public void getTypeNameShouldNeverAcceptNullType() {
        expect(() -> new ConfigurableCodeStyle.Builder().build().getTypeName(null)).toThrow(AssertionError.class);
    }
}
